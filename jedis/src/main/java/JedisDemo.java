import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Set;

public class JedisDemo {
    public static void main(String[] args) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println(jedis.ping());

        // 设置 redis 字符串数据
        jedis.set("site", "baidu.com");
        System.out.println(jedis.get("site"));

        // 存储数据到列表中
        jedis.lpush("language", "Java");
        jedis.lpush("language", "Golang");
        List<String> list = jedis.lrange("language", 0, 1);
        for (String s : list) {
            System.out.println(s);
        }

        // 获取数据并输出
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        // 基于setnx实现分布式锁
        String result = jedis.set("lock_test", "value", SetParams.setParams().nx());
        System.out.println("第一次加锁的结果: " + result); // OK

        result = jedis.set("lock_test", "value", SetParams.setParams().nx());
        System.out.println("第二次加锁的结果: " + result); // null

        // 删除分布式锁
        jedis.del("lock_test");

        result = jedis.set("lock_test", "value", SetParams.setParams().nx());
        System.out.println("第三次加锁的结果: " + result); // OK

    }
}
