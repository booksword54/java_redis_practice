package list;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 注册之后发送邮件
 */
public class SendMailDemo {

    private Jedis jedis = new Jedis("localhost");

    /**
     * 让发送邮件任务入队列
     */
    public void enqueueSendMailTask(String sendMailTask) {
        jedis.lpush("send_mail_task_queue", sendMailTask);
    }

    /**
     * 阻塞式获取发送邮件任务
     */
    public List<String> takeSendMailTask() {
        // 阻塞五秒，再从list里面拿
        return jedis.brpop(5, "send_mail_task_queue");
    }

    public static void main(String[] args) {
        SendMailDemo demo = new SendMailDemo();
        List<String> sendMailTasks = demo.takeSendMailTask();

        demo.enqueueSendMailTask("第一个发送邮件任务");
        sendMailTasks = demo.takeSendMailTask();
        System.out.println(sendMailTasks);

    }

}
