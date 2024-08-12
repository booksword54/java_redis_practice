package list;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

import java.util.List;
import java.util.Random;

/**
 * OA系统待办事项的管理案例
 */
public class TodoEventDemo {

    Jedis jedis = new Jedis("localhost");

    /**
     * 添加待办事项
     */
    public void addTodoEvent(long userId, String todoEvent) {
        jedis.lpush("todo_event::" + userId, todoEvent);
    }

    /**
     * 分页查询待办事项列表
     */
    public List<String> findTodoEventByPage(long userId, int pageNo, int pageSize) {
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = pageNo * pageSize - 1;
        return jedis.lrange("todo_event::" + userId, startIndex, endIndex);
    }

    /**
     * 插入待办事项
     */
    public void insertTodoEvent(long userId,
                                ListPosition position,
                                String targetTodoEvent,
                                String newTodoEvent) {
        jedis.linsert("todo_event::" + userId, position, targetTodoEvent, newTodoEvent);
    }

    /**
     * 修改待办事项
     */
    public void updateTodoEvent(long userId, int index, String updatedTodoEvent) {
        jedis.lset("todo_event::" + userId, index, updatedTodoEvent);
    }

    /**
     * 完成待办事项
     */
    public void finishTodoEvent(long userId, String todoEvent) {
        // 0 都删掉，其他的是几个删几个
        jedis.lrem("todo_event::" + userId, 0, todoEvent);
    }

    /**
     * 批量完成待办事项
     */
    public void batchFinishTodoEvent(long userId) {
        // 删除最新的两个待办
        jedis.ltrim("todo_event::" + userId, 0, 1);
    }

    public static void main(String[] args) {
        TodoEventDemo demo = new TodoEventDemo();
        long userId = 4;
        for (int i = 1; i <= 20; i++) {
            demo.addTodoEvent(userId, "第" + i + "个待办事项");
        }
        List<String> todoEventPage = demo.findTodoEventByPage(userId, 1, 10);
        System.out.println("第一次查询第一页待办事项------------------");
        for (String todoEvent : todoEventPage) {
            System.out.println(todoEvent);
        }

        // 插入待办事项
        Random random = new Random();
        int index = random.nextInt(todoEventPage.size());
        String targetTodoEvent = todoEventPage.get(index);
        demo.insertTodoEvent(userId, ListPosition.BEFORE, targetTodoEvent, "插入的待办事项");
        System.out.println("在" + targetTodoEvent + "前面插入了一个待办事项");

        // 重新分页查询待办事项
        todoEventPage = demo.findTodoEventByPage(userId, 1, 10);
        System.out.println("第二次查询第一页待办事项------------------");
        for (String todoEvent : todoEventPage) {
            System.out.println(todoEvent);
        }

        // 修改待办事项
        index = random.nextInt(todoEventPage.size());
        demo.updateTodoEvent(userId, index, "修改后的待办事项");

        // 完成一个待办事项
        demo.finishTodoEvent(userId, todoEventPage.get(0));

        // 重新分页查询待办事项
        todoEventPage = demo.findTodoEventByPage(userId, 1, 10);
        System.out.println("第三次查询第一页待办事项------------------");
        for (String todoEvent : todoEventPage) {
            System.out.println(todoEvent);
        }
    }
}
