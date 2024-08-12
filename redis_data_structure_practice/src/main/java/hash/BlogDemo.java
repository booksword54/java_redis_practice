package hash;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * 基于hash实现博客网站案例
 */
public class BlogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 唯一ID生成器
     */
    public long getBlogId() {
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发表博客
     */
    public void publishBlog(long blogId, String title, String content, String author, String time) {
        jedis.msetnx("article:" + blogId + ":title", title,
                     "article:" + blogId + ":content", content,
                     "article:" + blogId + ":author", author,
                     "article:" + blogId + ":time", time
        );
        // 博客长度统计
        long blogLen = jedis.strlen("article:" + blogId + ":content");
        jedis.setnx("article:" + blogId + ":content_length", String.valueOf(blogLen));
    }

    /**
     * 查看博客
     */
    public List<String> getBlog(long blogId) {
        List<String> blog = jedis.mget("article:" + blogId + ":title",
                                       "article:" + blogId + ":content",
                                       "article:" + blogId + ":author",
                                       "article:" + blogId + ":time",
                                       "article:" + blogId + ":content_length",
                                       "article:" + blogId + ":like_count",
                                       "article:" + blogId + ":view_count"
        );
        increaseViewCount(blogId); // 点击次数加一
        blog.add(String.valueOf(blogId));
        return blog;
    }

    /**
     * 修改博客
     */
    public void updateBlog(long blogId, String title, String content) {
        jedis.mset("article:" + blogId + ":title", title,
                   "article:" + blogId + ":content", content
        );
        // 博客长度统计
        long blogLen = jedis.strlen("article:" + blogId + ":content");
        jedis.setnx("article:" + blogId + ":content_length", String.valueOf(blogLen));
    }

    /**
     * 预览博客
     */
    public String previewBlog(long blogId) {
        return jedis.getrange("article:" + blogId + ":content", 0, 11); // 字节起止 [0,11]
    }

    /**
     * 点赞
     */
    public void likeBlog(long blogId) {
        jedis.incr("article:" + blogId + ":like_count");
    }

    /**
     * 增加博客浏览次数
     */
    public void increaseViewCount(long blogId) {
        jedis.incr("article:" + blogId + ":view_count");
    }

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();
        // 发布博客
        long blogId = demo.getBlogId();
        String title = "我喜欢学习Redis";
        String content = "学习Redis是一件快乐的事情";
        String author = "小华";
        String time = "2024-10-01";
        demo.publishBlog(blogId, title, content, author, time);

        // 更新博客
        String updatedTitle = "我喜欢学习Java";
        String updatedContent = "学习Java是一件快乐的事情";
        demo.updateBlog(blogId, updatedTitle, updatedContent);

        // 预览博客
        String previewBlog = demo.previewBlog(blogId);
        System.out.println("预览内容: " + previewBlog);

        // 查看博客详情
        List<String> blog = demo.getBlog(blogId);
        System.out.println("博客详情: " + blog);

        // 点赞
        demo.likeBlog(blogId);

        // 查看博客浏览次数和点赞次数
        blog = demo.getBlog(blogId);
        System.out.println("浏览次数和点赞次数: " + blog);
    }
}
