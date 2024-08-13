package other.expire;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 支持自动过期的用户登录会话实现
 */
public class SessionDemo {

    private Jedis jedis = new Jedis("localhost");

    /**
     * 登录
     */
    public String login(String username, String password) {
        Random random = new Random();
        // 用户id
        long userId = random.nextInt() * 100L;
        // 登录成功后，生成一块令牌
        String token = UUID.randomUUID()
                .toString()
                .replace("-", "");
        // 基于用户id和令牌初始化session
        jedis.set("session::" + token, String.valueOf(userId));
        jedis.expire("session::" + token, 10);
        // 返回token给用户
        return token;
    }

    /**
     * 检查Session是否有效
     */
    public boolean isSessionValid(String token) throws ParseException {
        if (token == null || "".equals(token)) {
            return false;
        }
        String session = jedis.get("session::" + token);
        if (session == null || "".equals(session)) {
            return false;
        }
        // do something
        return true;
    }

    public static void main(String[] args) throws ParseException, InterruptedException {
        SessionDemo demo = new SessionDemo();
        boolean isSessionValid = demo.isSessionValid(null);
        // 第一访问系统, Session校验结果: false
        System.out.println("第一访问系统, Session校验结果: " + isSessionValid);

        String token = demo.login("zhangsan", "12345");
        // 登录过后拿到token: 3e6d72ad4d1e4ab0aefe0cc8a4b79a6a
        System.out.println("登录过后拿到token: " + token);

        isSessionValid = demo.isSessionValid(token);
        // 第二次访问系统的session的校验结果：true
        System.out.println("第二次访问系统的session的校验结果：" + isSessionValid);

        Thread.sleep(12 * 1000);
        isSessionValid = demo.isSessionValid(token);
        // 第三次访问系统的session的校验结果：false
        System.out.println("第三次访问系统的session的校验结果：" + isSessionValid);

    }
}
