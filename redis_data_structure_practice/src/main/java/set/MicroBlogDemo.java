package set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 微博案例
 */
public class MicroBlogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 关注
     */
    public void follow(long userId, long followUserId) {
        jedis.sadd("user::" + followUserId + "::followers", String.valueOf(userId));
        jedis.sadd("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 取消关注
     */
    public void unfollow(long userId, long followUserId) {
        jedis.srem("user::" + followUserId + "::followers", String.valueOf(userId));
        jedis.srem("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 查看有哪些人关注
     */
    public Set<String> getFollowers(long userId) {
        return jedis.smembers("user::" + userId + "::followers");
    }

    /**
     * 有多少人关注
     */
    public long getFollowersCount(long userId) {
        return jedis.scard("user::" + userId + "::followers");
    }

    /**
     * 查看有关注哪些人
     */
    public Set<String> getFollowerUsers(long userId) {
        return jedis.smembers("user::" + userId + "::follow_users");
    }

    /**
     * 关注多少人
     */
    public long getFollowerUsersCount(long userId) {
        return jedis.scard("user::" + userId + "::follow_users");
    }

    public static void main(String[] args) {
        MicroBlogDemo demo = new MicroBlogDemo();

        long userId = 1;
        long friendId = 2;
        long superStarId = 3;

        demo.follow(userId, friendId);
        demo.follow(userId, superStarId);
        demo.follow(friendId, superStarId);

        Set<String> superStarFollowers = demo.getFollowers(superStarId);
        long superStarFollowersCount = demo.getFollowersCount(superStarId);
        // 明星粉丝数: 2，粉丝有[1, 2]
        System.out.println("明星粉丝数: " + superStarFollowersCount + "，粉丝有" + superStarFollowers);

        Set<String> friendFollowUsers = demo.getFollowerUsers(friendId);
        long friendFollowUsersCount = demo.getFollowerUsersCount(friendId);
        // 朋友关注几个: 1，关注了[3]
        System.out.println("朋友关注几个: " + friendFollowUsersCount + "，关注了" + friendFollowUsers);

        Set<String> userFollowUsers = demo.getFollowerUsers(userId);
        long userFollowUsersCount = demo.getFollowerUsersCount(userId);
        // 用户关注几个: 2，关注了[2, 3]
        System.out.println("用户关注几个: " + userFollowUsersCount + "，关注了" + userFollowUsers);
    }
}
