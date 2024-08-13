package geo;

import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

/**
 * 用户与商家的距离计算案例
 */
public class UserShopDistanceDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加一个地理位置
     */
    public void addLocation(String name, double longitude, double latitude) {
        jedis.geoadd("location_data", longitude, latitude, name);
    }

    /**
     * 计算地理距离
     */
    public double getDistance(String user, String shop) {
        return jedis.geodist("location_data", user, shop, GeoUnit.KM);
    }

    public static void main(String[] args) {
        UserShopDistanceDemo demo = new UserShopDistanceDemo();
        demo.addLocation("张三", 116.49428833935545, 39.86700462665782);
        demo.addLocation("小吃店", 116.45961274121092, 39.87517301328063);
        System.out.println("用户到商家的距离: " + demo.getDistance("张三", "小吃店") + " km");
    }

}
