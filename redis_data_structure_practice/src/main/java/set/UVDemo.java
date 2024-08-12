package set;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 网站UV统计案例
 */
public class UVDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加一次用户访问记录
     */
    public void addUserAccess(long userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = simpleDateFormat.format(new Date());
        jedis.sadd("user_access_set::" + today, String.valueOf(userId));
    }

    /**
     * 获取当天网站uv的值
     */
    public long getUV() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = simpleDateFormat.format(new Date());
        return jedis.scard("user_access_set::" + today);
    }

    public static void main(String[] args) {
        UVDemo demo = new UVDemo();
        for (int i = 0; i < 100; i++) {
            long userId = i + 1;
            for (int j = 0; j < 10; j++) {
                demo.addUserAccess(userId); // 对userId去重
            }
        }
        long uv = demo.getUV();
        System.out.println("当日UV为: " + uv); // 100
    }
}
