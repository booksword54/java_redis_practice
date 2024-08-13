package sorted_set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 * 音乐排行榜案例
 */
public class MusicRankingListDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 把新的音乐加入到排行榜里面去
     */
    public void addMusic(long musicId) {
        jedis.zadd("music_ranking_list", 0, String.valueOf(musicId));
    }

    /**
     * 增加歌曲的分数
     */
    public void increaseMusicScore(long musicId, double score) {
        jedis.zincrby("music_ranking_list", score, String.valueOf(musicId));
    }

    /**
     * 获取音乐排名
     */
    public long getMusicRank(long musicId) {
        return jedis.zrevrank("music_ranking_list", String.valueOf(musicId));
    }

    /**
     * 获取音乐排行榜
     */
    public Set<Tuple> getMusicRankingList() {
        return jedis.zrevrangeWithScores("music_ranking_list", 0, 2);
    }

    public static void main(String[] args) {
        MusicRankingListDemo demo = new MusicRankingListDemo();
        for (int i = 0; i < 20; i++) {
            demo.addMusic(i + 1);
        }
        demo.increaseMusicScore(5, 3.2);
        demo.increaseMusicScore(15, 5.6);
        demo.increaseMusicScore(7, 9.6);

        long musicRank = demo.getMusicRank(5);
        System.out.println("id: 5, rank: " + (musicRank + 1));
        // id: 5, rank: 3
        Set<Tuple> musicRankingList = demo.getMusicRankingList();
        // 歌曲排名Top3: [[7,9.6], [15,5.6], [5,3.2]]
        System.out.println("歌曲排名Top3: " + musicRankingList);
    }
}
