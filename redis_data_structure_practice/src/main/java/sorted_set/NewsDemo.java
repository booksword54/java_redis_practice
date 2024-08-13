package sorted_set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Date;
import java.util.Set;

/**
 * 新闻浏览案例
 */
public class NewsDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 加入新闻，分值是时间戳
     */
    public void addNews(long newsId, long timestamp) {
        jedis.zadd("news", timestamp, String.valueOf(newsId));
    }

    /**
     * 按时间范围分页搜索新闻
     */
    public Set<Tuple> searchNews(long maxTimestamp, long minTimestamp, int index, int count) {
        return jedis.zrevrangeByScoreWithScores("news", maxTimestamp, minTimestamp, index, count);
    }

    public static void main(String[] args) {
        NewsDemo demo = new NewsDemo();
        for (int i = 0; i < 20; i++) {
            demo.addNews(i + 1, i + 1);
        }

        long maxTimestamp = 18;
        long minTimestamp = 2;
        int pageNo = 1;
        int pageSize = 5;
        int startIndex = (pageNo - 1) * pageSize;

        Set<Tuple> result = demo.searchNews(maxTimestamp, minTimestamp, startIndex, pageSize);
        // 指定时间范围的第一页新闻: [[18,18.0], [17,17.0], [16,16.0], [15,15.0], [14,14.0]]
        System.out.println("指定时间范围的第一页新闻: " + result);
    }
}
