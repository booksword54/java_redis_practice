package hyperloglog;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 网站日常指标统计案例
 * 日、周活跃用户数
 */
public class WebsiteStatisticDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 初始化某一天的UV数据
     */
    public void initUVData(String date) {
        Random random = new Random();
        int startIndex = random.nextInt(1000);
        System.out.println("今日访问uv起始id为: " + startIndex);
        for (int i = startIndex; i < startIndex + 1358; i++) {
            // hyperloglog会进行去重
            for (int j = 0; j < 10; j++) {
                jedis.pfadd("hyperloglog_uv_" + date, String.valueOf(i + 1));
            }
        }
    }

    /**
     * 获取某一天的UV值
     */
    public long getUV(String date) {
        return jedis.pfcount("hyperloglog_uv_" + date);
    }

    /**
     * 获取某一周的UV值
     */
    public long getWeeklyUV() {
        List<String> keys = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String date = dateFormat.format(calendar.getTime());
            keys.add("hyperloglog_uv_" + date);
        }
        String[] keyArray = keys.toArray(new String[0]);
        jedis.pfmerge("weekly_uv", keyArray); // 合并去重一周的UV到weekly_uv
        return jedis.pfcount("weekly_uv");
    }

    public static void main(String[] args) {
        WebsiteStatisticDemo demo = new WebsiteStatisticDemo();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // 日活用户数
        long duplicateUV = 0;
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String date = dateFormat.format(calendar.getTime());
            demo.initUVData(date);
            long uv = demo.getUV(date);
            System.out.println("日期: " + date + ", UV数: " + uv);

            duplicateUV += uv;
        }
        System.out.println("没有去重用户的周活跃UV: " + duplicateUV);

        // 周活用户数
        long weeklyUV = demo.getWeeklyUV();
        System.out.println("实际的周活跃UV: " + weeklyUV);
    }

}
