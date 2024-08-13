package geo;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 附近的人的案例
 */
public class NearbyUsersDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加一个用户
     */
    public void addUser(String user, double longitude, double latitude) {
        jedis.geoadd("user_location_data", longitude, latitude, user);
    }

    /**
     * 获取附近5KM的人
     */
    public List<GeoRadiusResponse> getNearbyUsers(String user) {
        return jedis.georadiusByMember("user_location_data", user, 5.0, GeoUnit.KM);
    }

    public static void main(String[] args) {
        NearbyUsersDemo demo = new NearbyUsersDemo();
        demo.addUser("张三", 116.49428833935545, 39.86700462665782);
        demo.addUser("李四", 116.45961274121092, 39.87517301328063);
        List<GeoRadiusResponse> nearbyUsers = demo.getNearbyUsers("张三");
        System.out.println("张三附近5KM的人: " + nearbyUsers);
    }
}
