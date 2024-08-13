package other.expire;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * 支持身份验证功能的超时自动释放的分布式锁案例
 */
public class TimeoutDistributedLockDemo {

    private static Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 加锁，指定user
     */
    public boolean lock(String key, String user, int timeout) {
        Long result = jedis.setnx(key, user);
        jedis.expire(key, timeout);
        return result > 0;
    }

    /**
     * 解锁，需要进行身份验证
     */
    public boolean unlock(String key, String user) {
        String currentUser = jedis.get(key);
        try (Pipeline pipeline = jedis.pipelined()) {
            pipeline.watch(user);
            if (currentUser == null || currentUser.equals("")) {
                return true;
            }
            if (!currentUser.equals(user)) {
                return false;
            }
            pipeline.multi();
            pipeline.del(key);
            pipeline.exec();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TimeoutDistributedLockDemo demo = new TimeoutDistributedLockDemo();
        boolean result = demo.lock("test_lock", "张三", 10);
        // 张三加锁的结果: true
        System.out.println("张三加锁的结果: " + result);

        boolean unlock = demo.unlock("test_lock", "李四");
        // 李四解锁的结果: false
        System.out.println("李四解锁的结果: " + unlock);

        unlock = demo.unlock("test_lock", "张三");
        // 张三解锁的结果: true
        System.out.println("张三解锁的结果: " + unlock);
    }

}
