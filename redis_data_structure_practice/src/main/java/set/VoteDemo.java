package set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 投票统计案例
 */
public class VoteDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 投票
     */
    public void vote(long userId, long voteItemId) {
        jedis.sadd("vote_item_users::" + voteItemId, String.valueOf(userId));
    }

    /**
     * 用户是否投票
     */
    public boolean hasVoted(long userId, long voteItemId) {
        return jedis.sismember("vote_item_users::" + voteItemId, String.valueOf(userId))
    }

    /**
     * 获取一个投票项有哪些人投票了
     */
    public Set<String> getVoteItemUsers(long voteItemId) {
        return jedis.smembers("vote_item_users::" + voteItemId);
    }

    /**
     * 获取一个投票项有多少人投票
     */
    public long getVoteItemUsersCount(long voteItemId) {
        return jedis.scard("vote_item_users::" + voteItemId);
    }

    public static void main(String[] args) {
        // 定义用户ID
        long userId = 1;
        // 投票项Id
        long voteItemId = 110;

        VoteDemo demo = new VoteDemo();
        demo.vote(userId, voteItemId);
        boolean hasVoted = demo.hasVoted(userId, voteItemId);

        Set<String> voteItemUsers = demo.getVoteItemUsers(voteItemId);
        long voteItemUsersCount = demo.getVoteItemUsersCount(voteItemId);
        System.out.println("投几票了: " + voteItemUsersCount + " 都有谁: " + voteItemUsers);
    }
}
