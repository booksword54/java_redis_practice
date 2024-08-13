package other.expire;

import redis.clients.jedis.Jedis;

/**
 * 数据自动过期案例
 */
public class ExpireDemo {

    private static Jedis jedis = new Jedis("127.0.0.1");

    public static void main(String[] args) throws InterruptedException {
        //jedis.set("test_key", "test_value");
        //jedis.expire("test_key", 10); // 10 seconds
        // 可以合并为一个setex指令
        jedis.setex("test_key", 10, "test_value");

        String before = jedis.get("test_key");
        // 数据是否过期: false
        System.out.println("数据是否过期: " + (before == null));

        Thread.sleep(12 * 1000);

        String after = jedis.get("test_key");
        // 数据是否过期: true
        System.out.println("数据是否过期: " + (after == null));
    }
}
