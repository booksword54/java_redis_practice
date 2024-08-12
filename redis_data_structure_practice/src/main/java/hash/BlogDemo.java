package hash;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
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
    public void publishBlog(long blogId, Map<String, String> blog) {
        if (jedis.hexists("article:" + blogId, "title")) {
            return;
        }
        jedis.hmset("article::" + blogId, blog);
    }

    /**
     * 查看博客
     */
    public Map<String, String> findBlogById(long blogId) {
        Map<String, String> blog = jedis.hgetAll("article::" + blogId);
        increaseBlogViewCount(blogId); // 点击次数加一
        return blog;
    }

    /**
     * 修改博客
     */
    public void updateBlog(long blogId, Map<String, String> updatedBlog) {
        String updatedContent = updatedBlog.get("content");
        if (updatedContent != null && !"".equals(updatedContent)) {
            updatedBlog.put("content_length", String.valueOf(updatedContent.length()));
        }
        jedis.hmset("article::" + blogId, updatedBlog);
    }

    /**
     * 预览博客 hash结构暂时没有 hgetrange
     */
    //public String previewBlog(long blogId) {
    //    return jedis.getrange("article:" + blogId + ":content", 0, 11); // 字节起止 [0,11]
    //}

    /**
     * 点赞
     */
    public void increaseBlogLikeCount(long blogId) {
        jedis.hincrBy("article::" + blogId, "like_count", 1);
    }

    /**
     * 增加博客浏览次数
     */
    public void increaseBlogViewCount(long blogId) {
        jedis.hincrBy("article::" + blogId, "view_count", 1);
    }

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();
        // 发布博客
        long blogId = demo.getBlogId();
        String title = "我喜欢学习Redis";
        String content = "学习Redis是一件快乐的事情";
        String author = "小华";
        String time = "2024-10-01";

        Map<String, String> blog = new HashMap<>();
        blog.put("id", String.valueOf(blogId));
        blog.put("title", title);
        blog.put("content", content);
        blog.put("author", author);
        blog.put("time", time);
        blog.put("content_length", String.valueOf(content.length()));

        demo.publishBlog(blogId, blog);

        // 更新博客
        String updatedTitle = "我喜欢学习Java";
        String updatedContent = "学习Java是一件快乐的事情";

        Map<String, String> updatedBlog = new HashMap<>();
        updatedBlog.put("id", String.valueOf(blogId));
        updatedBlog.put("title", updatedTitle);
        updatedBlog.put("content", updatedContent);

        demo.updateBlog(blogId, updatedBlog);

        // 查看博客详情
        System.out.println("博客详情: " + demo.findBlogById(blogId));

        // 点赞
        demo.increaseBlogLikeCount(blogId);

        // 查看博客浏览次数和点赞次数
        System.out.println("浏览次数和点赞次数: " +  demo.findBlogById(blogId));
    }
}
