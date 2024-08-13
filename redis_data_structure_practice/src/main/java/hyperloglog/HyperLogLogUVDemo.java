package hyperloglog;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基于HyperLogLog统计UV的案例
 */
public class HyperLogLogUVDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 初始化UV数据
     */
    public void initUVData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        for (int i = 0; i < 1358; i++) {
            // hyperloglog会进行去重
            for (int j = 0; j < 10; j++) {
                jedis.pfadd("hyperloglog_uv_" + today, String.valueOf(i + 1));
            }
        }
    }

    /**
     * 获取UV的值
     * 有0.8%的误差
     */
    public long getUV() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        return jedis.pfcount("hyperloglog_uv_" + today);
    }

    public static void main(String[] args) {
        HyperLogLogUVDemo demo = new HyperLogLogUVDemo();
        demo.initUVData();
        long uv = demo.getUV();
        // 今天的uv是: 1366
        System.out.println("今天的uv是: " + uv);
    }
}
