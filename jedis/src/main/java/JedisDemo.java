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
        String result = jedis.set("lock_test", "value", SetParams.setParams()
                .nx());
        System.out.println("第一次加锁的结果: " + result); // OK

        result = jedis.set("lock_test", "value", SetParams.setParams()
                .nx());
        System.out.println("第二次加锁的结果: " + result); // null

        // 删除分布式锁
        jedis.del("lock_test");

        result = jedis.set("lock_test", "value", SetParams.setParams()
                .nx());
        System.out.println("第三次加锁的结果: " + result); // OK

        // 基于msetnx、mget和mset实现文章博客的发布、查看与修改
        // 发布
        long msetnxResult = jedis.msetnx("article:1:title", "学习Redis",
                                         "article:1:content", "如何学好Redis",
                                         "article:1:author", "小华",
                                         "article:1:time", "2024-08-01");
        System.out.println("发布博客的结果: " + msetnxResult); // 发布博客的结果: 1

        // 查看
        List<String> blog = jedis.mget("article:1:title",
                                       "article:1:content",
                                       "article:1:author",
                                       "article:1:time");
        System.out.println("查看博客: " + blog); // 查看博客: [学习Redis, 如何学好Redis, 小华, 2024-08-01]

        // 修改
        String updateBlogResult = jedis.mset("article:1:title", "学习Java",
                                             "article:1:content", "如何学好Java",
                                             "article:1:author", "小龙",
                                             "article:1:time", "2024-10-01");
        System.out.println("修改博客的结果: " + updateBlogResult); // 修改博客的结果: OK

        // 查看
        blog = jedis.mget("article:1:title",
                          "article:1:content",
                          "article:1:author",
                          "article:1:time");
        System.out.println("查看博客: " + blog); // 查看博客: [学习Java, 如何学好Java, 小龙, 2024-10-01]

        // 基于strlen、getrange实现博客长度统计与文章预览
        // 长度统计
        Long blogLen = jedis.strlen("article:1:content");
        System.out.println("博客长度统计: " + blogLen); // 16字节

        // 预览
        String contentPreview = jedis.getrange("article:1:content", 0, 5); // 字节起止 0 - 5
        System.out.println("预览: " + contentPreview); // 如何

        // 基于append实现用户追加日志审计
        jedis.setnx("operation_log", "");
        for (int i = 0; i < 10; i++) {
            jedis.append("operation_log", "第" + i + "条日志");
        }
        String log = jedis.get("operation_log");
        System.out.println("所有操作日志:\n" + blog);

        // 基于incr实现实现唯一ID生成器
        for (int i = 1; i <= 10; i++) {
            Long orderId = jedis.incr("order_id_counter");
            System.out.println("生成的第" + i + "个唯一ID: " + orderId);
        }

        // 基于incr和decr实现博客点赞和取消
        // 点赞
        for (int i = 1; i <= 10; i++) {
            jedis.incr("article:1:like_number");
            // jedis.incrBy("article:1:likeNumber", 1);
        }
        long likeNumber = Long.parseLong(jedis.get("article:1:like_number"));
        System.out.println("点赞次数为: " + likeNumber);

        // 取消
        jedis.decr("article:1:like_number");
        // jedis.decrBy("article:1:likeNumber", 1);
        likeNumber = Long.parseLong(jedis.get("article:1:like_number"));
        System.out.println("点赞次数为: " + likeNumber);

    }
}
