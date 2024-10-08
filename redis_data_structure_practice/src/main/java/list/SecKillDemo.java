package list;

import redis.clients.jedis.Jedis;

/**
 * 秒杀活动案例
 */
public class SecKillDemo {

    private Jedis jedis = new Jedis("localhost");

    /**
     * 秒杀抢购请求入队
     */
    public void enqueueSecKillRequest(String secKillRequest) {
        jedis.lpush("sec_kill_request_queue", secKillRequest);
    }

    /**
     * 秒杀抢购请求出队
     */
    public String dequeSecKillRequest() {
        return jedis.rpop("sec_kill_request_queue");
    }

    public static void main(String[] args) {
        SecKillDemo demo = new SecKillDemo();
        for (int i = 1; i <= 10; i++) {
            demo.enqueueSecKillRequest("第" + i + "个秒杀请求");
        }
        while (true) {
            String secKillRequest = demo.dequeSecKillRequest();
            if (secKillRequest == null
                    || "null".equals(secKillRequest)
                    || "".equals(secKillRequest)) {
                break;
            }
            System.out.println(secKillRequest);
        }
    }
}
