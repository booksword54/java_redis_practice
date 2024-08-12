package set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 朋友圈点赞案例
 */
public class MomentDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 对朋友圈进行点赞
     */
    public void likeMoment(long userId, long momentId) {
        jedis.sadd("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 取消点赞
     */
    public void dislikeMoment(long userId, long momentId) {
        jedis.srem("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 查看是否对某条朋友圈点赞
     */
    public boolean hasLikeMoment(long userId, long momentId) {
        return jedis.sismember("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 查看某条朋友圈有哪些人点赞
     */
    public Set<String> getMomentLikeUsers(long momentId) {
        return jedis.smembers("moment_like_users::" + momentId);
    }

    /**
     * 查看某条朋友圈有几个赞
     */
    public long getMomentLikeUsersCount(long momentId) {
        return jedis.scard("moment_like_users::" + momentId);
    }

    public static void main(String[] args) {
        MomentDemo demo = new MomentDemo();
        long momentId = 150;
        long friendId = 2;
        long otherFriendId = 3;

        // 朋友点赞
        demo.likeMoment(friendId, momentId);
        demo.likeMoment(otherFriendId, momentId);

        // 朋友取消点赞
        demo.dislikeMoment(friendId, momentId);

        boolean hasLikeMoment = demo.hasLikeMoment(friendId, momentId);
        System.out.println("朋友2是否点赞: " + hasLikeMoment); // false
        hasLikeMoment = demo.hasLikeMoment(otherFriendId, momentId);
        System.out.println("朋友3是否点赞: " + hasLikeMoment); // true

        // 查看朋友圈点赞朋友
        Set<String> momentLikeUsers = demo.getMomentLikeUsers(momentId);
        System.out.println("点赞的朋友有: " + momentLikeUsers);

        // 查看朋友圈朋友点赞数
        long momentLikeUsersCount = demo.getMomentLikeUsersCount(momentId);
        System.out.println("点赞数: " + momentLikeUsersCount);
    }
}
