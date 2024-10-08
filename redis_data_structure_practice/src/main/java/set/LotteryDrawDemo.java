package set;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 抽奖案例
 */
public class LotteryDrawDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加抽奖候选人
     */
    public void addLotteryDrawCandidate(long userId, long lotteryDrawEventId) {
        jedis.sadd("lottery_draw_event::" + lotteryDrawEventId + "::candidates", String.valueOf(userId));
    }

    /**
     * 抽奖
     */
    public List<String> doLotteryDraw(long lotteryDrawEventId, int count) {
        return jedis.srandmember("lottery_draw_event::" + lotteryDrawEventId + "::candidates", count);
    }

    public static void main(String[] args) {
        LotteryDrawDemo demo = new LotteryDrawDemo();
        int lotteryDrawEventId = 120;
        for (int i = 0; i < 20; i++) {
            demo.addLotteryDrawCandidate(i + 1, lotteryDrawEventId);
        }
        List<String> lotteryDrawUsers = demo.doLotteryDraw(lotteryDrawEventId, 3);
        System.out.println(lotteryDrawUsers + "中奖了");
    }
}
