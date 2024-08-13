package set;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 商品搜索案例
 */
public class ProductSearchDemo {
    
    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加商品，关联关键词
     */
    public void addProduct(long productId, String[] keywords) {
        for (String keyword : keywords) {
            jedis.sadd("keyword::" + keyword + "::products", String.valueOf(productId));
        }
    }

    /**
     * 根据多个关键词搜索商品
     */
    public Set<String> searchProduct(String[] keywords) {
        List<String> keywordSetKeys = new ArrayList<>();
        for (String keyword : keywords) {
            keywordSetKeys.add("keyword::" + keyword + "::products");
        }
        String[] keywordArray = keywordSetKeys.toArray(new String[0]);
        return jedis.sinter(keywordArray);
    }

    public static void main(String[] args) {
        ProductSearchDemo demo = new ProductSearchDemo();

        // 添加一批商品
        demo.addProduct(1, new String[]{"手机", "iphone", "潮流"});
        demo.addProduct(2, new String[]{"炫酷", "iphone", "潮流"});
        demo.addProduct(3, new String[]{"蓝色", "iphone"});

        // 根据关键词搜索商品
        Set<String> result = demo.searchProduct(new String[]{"iphone", "潮流"});
        // 商品搜索结果: [1, 2]
        System.out.println("商品搜索结果: " + result);
    }
}
