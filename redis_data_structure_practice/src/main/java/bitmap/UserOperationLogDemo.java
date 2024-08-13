package bitmap;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 网站用户操作日志案例
 */
public class UserOperationLogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 记录用户操作日志
     */
    public void recordUserOperationLog(String operation, long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        jedis.setbit("operation::" + operation + "::" + today + "::log",
                     userId,
                     String.valueOf(1));
    }

    /**
     * 判断用户今天是否执行过某个操作
     */
    public boolean hasOperated(String operation, long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        return jedis.getbit("operation::" + operation + "::" + today + "::log", userId);
    }

    public static void main(String[] args) {
        UserOperationLogDemo demo = new UserOperationLogDemo();
        long userId = 110;
        // 是否执行过操作: false
        System.out.println("是否执行过操作: " + (demo.hasOperated("操作1", userId)));
        demo.recordUserOperationLog("操作1", userId);
        // 是否执行过操作: true
        System.out.println("是否执行过操作: " + (demo.hasOperated("操作1", userId)));
    }
}
