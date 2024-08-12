package hash;

import redis.clients.jedis.Jedis;

/**
 * 基于hash数据结构实现短网址点击追踪案例
 */
public class ShortUrlDemo {
    private static final String X36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");

    private Jedis jedis = new Jedis("127.0.0.1");

    public ShortUrlDemo() {
        jedis.set("short_url_seed", "54564516556");
    }

    /**
     * 获取短链接网址
     * 利用redis的incr自增长，然后10进制转36进制，
     * 接着hset存放在hash数据结构里，再提供一个映射转换的hget获取方法
     */
    public String getShortUrl(String url) {
        Long shortUrlSeed = jedis.incr("short_url_seed"); // seed越大，短链接越长，种子自增长，每次生成的短链接都是不同的
        StringBuilder stringBuilder = new StringBuilder();

        while (shortUrlSeed > 0) {
            stringBuilder.append(X36_ARRAY[(int) (shortUrlSeed % 36)]);
            shortUrlSeed = shortUrlSeed / 36;
        }

        String shortUrl = stringBuilder.reverse().toString();
        // 短链接访问次数
        jedis.hset("short_url_access_count", shortUrl, "0");
        // 短链接与长连接映射关系
        jedis.hset("url_mapping", shortUrl, url);

        return shortUrl;
    }

    /**
     * 短链接访问次数增长
     */
    public void incrementShortUrlAccessCount(String shortUrl) {
        jedis.hincrBy("short_url_access_count", shortUrl, 1);
    }

    /**
     * 获取短链接访问次数
     */
    public long getShortUrlAccessCount(String shortUrl) {
        return Long.parseLong(jedis.hget("short_url_access_count", shortUrl));
    }

    public static void main(String[] args) {
        ShortUrlDemo demo = new ShortUrlDemo();
        String shortUrl = demo.getShortUrl("www.baidu.com");
        System.out.println("短链接地址: " + shortUrl);

        // 访问短链接
        for (int i = 0; i < 200; i++) {
            demo.incrementShortUrlAccessCount(shortUrl);
        }

        // 获取访问次数
        long shortUrlAccessCount = demo.getShortUrlAccessCount(shortUrl);
        System.out.println("短链接访问次数: " + shortUrlAccessCount);
    }
}
