package sorted_set;

import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;

/**
 * 网站搜索自动补全案例
 */
public class AutoCompleteDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 搜索某个关键词
     */
    public void search(String keyword) {
        char[] keywordCharArray = keyword.toCharArray();
        StringBuilder potentialKeyword = new StringBuilder();
        for (char keywordChar : keywordCharArray) {
            potentialKeyword.append(keywordChar);
            jedis.zincrby("potential_keyword::" + potentialKeyword + "::keywords",
                          new Date().getTime(),
                          keyword);
        }
    }

    /**
     * 获取自动补全列表
     */
    public Set<String> getAutoCompleteList(String potentialKeyword) {
        return jedis.zrevrange("potential_keyword::" + potentialKeyword + "::keywords", 0, 2);
    }

    public static void main(String[] args) {
        AutoCompleteDemo demo = new AutoCompleteDemo();
        demo.search("我爱大家");
        demo.search("我喜欢Redis");
        demo.search("我喜欢Google");
        demo.search("我不太喜欢玩");

        Set<String> autoCompleteList = demo.getAutoCompleteList("我");
        // 第一次搜索推荐: [我不太喜欢玩, 我喜欢Google, 我喜欢Redis]
        System.out.println("第一次搜索推荐: " + autoCompleteList);

        autoCompleteList = demo.getAutoCompleteList("我喜");
        // 第二次搜索推荐: [我喜欢Google, 我喜欢Redis]
        System.out.println("第二次搜索推荐: " + autoCompleteList);

    }
}
