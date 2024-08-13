package hyperloglog;

import redis.clients.jedis.Jedis;

/**
 * 垃圾内容过滤案例
 */
public class GarbageContentFilterDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 判断当前内容是否是垃圾内容
     */
    public boolean isGarbageContent(String content) {
        return jedis.pfadd("hyperloglog_content", content) == 0;
    }

    public static void main(String[] args) {
        GarbageContentFilterDemo demo = new GarbageContentFilterDemo();
        String content = "正常内容";
        // 是否垃圾内容: false
        System.out.println("是否垃圾内容: " + demo.isGarbageContent(content));

        content = "重复内容";
        // 是否垃圾内容: false
        System.out.println("是否垃圾内容: " + demo.isGarbageContent(content));
        content = "重复内容";
        // 是否垃圾内容: true
        System.out.println("是否垃圾内容: " + demo.isGarbageContent(content));
    }
}
