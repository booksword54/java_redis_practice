package hash;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 用户会话管理案例
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
        initSession(userId, token);
        // 返回token给用户
        return token;
    }

    /**
     * 初始化Session
     */
    public void initSession(long userId, String token) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 24);

        Date expireTime = calendar.getTime();

        jedis.hset("sessions", "session::" + token, String.valueOf(userId));
        jedis.hset("sessions::expire_time", "session::" + token, format.format(expireTime));
    }

    /**
     * 检查Session是否有效
     */
    public boolean isSessionValid(String token) throws ParseException {
        if (token == null || "".equals(token)) {
            return false;
        }
        // session可能是json字符串
        // 简化一下，只放一个user_id作为value
        String session = jedis.hget("sessions", "session::" + token);
        if (session == null || "".equals(session)) {
            return false;
        }
        String expireTime = jedis.hget("sessions::expire_time", "session::" + token);
        if (expireTime == null || "".equals(expireTime)) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expireDate = format.parse(expireTime);
        Date now = new Date();
        if (now.after(expireDate)) {
            return false;
        }
        // do something
        return true;
    }

    public static void main(String[] args) throws ParseException {
        SessionDemo demo = new SessionDemo();
        // 第一次访问，token都是空的
        boolean isSessionValid = demo.isSessionValid(null);
        System.out.println("第一访问系统, Session校验结果: " + isSessionValid);

        // 登录
        String token = demo.login("zhangsan", "12345");
        System.out.println("登录过后拿到token: " + token);

        // 第二次访问系统。此时可以访问
        isSessionValid = demo.isSessionValid(token);
        System.out.println("第二次访问系统的session的校验结果：" + isSessionValid);

    }
}
