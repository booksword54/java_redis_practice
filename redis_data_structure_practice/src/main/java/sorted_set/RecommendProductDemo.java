package sorted_set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * 推荐其他商品案例
 */
public class RecommendProductDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 继续购买其它商品
     */
    public void continuePurchase(long productId, long otherProductId) {
        jedis.zincrby("continue_purchase_products::" + productId, 1, String.valueOf(otherProductId));
    }

    /**
     * 推荐其他人购买过的其他商品
     */
    public Set<Tuple> getRecommendProducts(long productId) {
        return jedis.zrevrangeWithScores("continue_purchase_products::" + productId, 0, 3);
    }

    public static void main(String[] args) {
        RecommendProductDemo demo = new RecommendProductDemo();
        long productId = 1;
        for (int i = 0; i < 20; i++) {
            demo.continuePurchase(productId, i + 2);
        }
        for (int i = 0; i < 4; i++) {
            demo.continuePurchase(productId, i + 2);
        }
        Set<Tuple> recommendProducts = demo.getRecommendProducts(productId);
        // 其他人也在买: [[5,4.0], [4,4.0], [3,4.0], [2,4.0]]
        System.out.println("其他人也在买: " + recommendProducts);
    }
}
