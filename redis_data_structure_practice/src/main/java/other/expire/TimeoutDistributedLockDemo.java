package other.expire;

import redis.clients.jedis.Jedis;

/**
 * 支持超时自动释放的分布式锁案例
 */
public class TimeoutDistributedLockDemo {

    private static Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 加锁
     */
    public boolean lock(String key, String value, int timeout) {
        Long result = jedis.setnx(key, value);
        jedis.expire(key, timeout);
        return result > 0;
    }

    /**
     * 解锁
     */
    public void unlock(String key) {
        jedis.del(key);
    }

    public static void main(String[] args) throws InterruptedException {
        TimeoutDistributedLockDemo demo = new TimeoutDistributedLockDemo();
        boolean result = demo.lock("test_lock", "test_value", 10);
        // 第一次加锁结果: true
        System.out.println("第一次加锁结果: " + result);

        result = demo.lock("test_lock", "test_value", 10);
        // 第二次加锁结果: false
        System.out.println("第二次加锁结果: " + result);

        Thread.sleep(12 * 1000);

        result = demo.lock("test_lock", "test_value", 10);
        // 第三次加锁结果: true
        System.out.println("第三次加锁结果: " + result);
    }

}
