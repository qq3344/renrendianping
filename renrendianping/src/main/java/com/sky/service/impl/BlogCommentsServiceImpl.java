package com.sky.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.sky.dto.Result;
import com.sky.dto.UserDTO;
import com.sky.pojo.BlogComments;
import com.sky.mapper.BlogCommentsMapper;
import com.sky.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.IBlogService;
import com.sky.utils.RedisConstants;
import com.sky.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IBlogService blogService;

    @Resource
    private BlogCommentsMapper blogCommentsMapper;

    /**
     * 博客评论
     *
     * @param blogComments
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveBlogComment(BlogComments blogComments) {
        // 评论添加到数据库
        this.save(blogComments);
        // tb_blog 评论数 + 1
        blogService.update().setSql("comments = comments + 1").eq("id", blogComments.getBlogId()).update();
        return Result.ok();
    }

    /**
     * 展示所有的评论
     *
     * @param blogId
     * @return
     */
    @Override
    public Result showBlogComments(Long blogId) {
        // 先将这篇博文下的所有评论数据全部查询出来，包括评论作者的信息，昵称和头像
        List<BlogComments> blogComments = blogCommentsMapper.findCommentDetail(blogId);
        // 获取所有的一级评论
        List<BlogComments> rootComments = blogComments.stream().filter(blogComments1 -> blogComments1.getParentId() == 0).collect(Collectors.toList());
        // 从一级评论中获取回复评论
        for (BlogComments rootComment : rootComments) {
            // 回复的评论
            List<BlogComments> comments = blogComments.stream()
                    .filter(blogComment -> blogComment.getParentId().equals(rootComment.getId()))
                    .collect(Collectors.toList());
            // 回复评论中含有父级评论的信息
            comments.forEach(comment -> {
                // 无法判断pComment是否存在，可以使用Optional
                Optional<BlogComments> pComment
                        = blogComments
                        .stream()
                        // 获取所有的评论的回复id也就是父级id的userid，这样就可以获取到父级评论的信息
                        .filter(blogComment -> comment.getAnswerId().equals(blogComment.getUserId())).findFirst();
                // 这里使用了Optional 只有pcomment！=null 的时候才会执行下面的代码
                pComment.ifPresent(v -> {
                    comment.setPNickName(v.getNickName());
                    comment.setPIcon(v.getIcon());
                });
                // 判断是否点赞
                isBlogCommentLiked(comment);
            });
            rootComment.setChildren(comments);
            // 判断是否点赞
            isBlogCommentLiked(rootComment);
        }
        return Result.ok(rootComments);
    }

    /**
     * 返回博客的总评论数
     * @param blogId
     * @return
     */
    @Override
    public Result getBlogCommentsTotal(Long blogId) {
        Long total = this.query().eq("blog_id", blogId).count();
        return Result.ok(total);
    }

    /**
     * 删除评论
     * @param blogCommentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteComment(Long blogCommentId) {
        // 先判断是否能够删除
        Long count = this.query().eq("parent_id", blogCommentId).count();
        // 不能删除，直接返回
        if (count > 0){
            return Result.ok("删除失败");
        }
        BlogComments blogComments = this.getById(blogCommentId);
        // 删除操作
        try {
            // 能删除
            this.removeById(blogCommentId);
            // 减少blogComment数量
            blogService.update().setSql("comments = comments - 1").eq("id",blogComments.getBlogId()).update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 评论点赞或者取消点赞
     * @param blogCommentId
     * @return
     */
    @Override
    public Result likeComment(Long blogCommentId) {
        // 获取当前登录用户信息
        UserDTO user = UserHolder.getUser();
        // 判断用户是否点赞
        Boolean flag = stringRedisTemplate.opsForSet().isMember(RedisConstants.BLOG_COMMENT_LIKED_KEY + blogCommentId.toString(), user.getId().toString());
        // 如果是没点赞，就是添加点赞数据
        if (BooleanUtil.isFalse(flag)){
            boolean success = this.update().setSql("liked = liked + 1").eq("id", blogCommentId).update();
            // 点赞成功，保存用户信息到redis
            if (success){
                stringRedisTemplate.opsForSet().add(RedisConstants.BLOG_COMMENT_LIKED_KEY+blogCommentId.toString(),user.getId().toString());
            }
        }else{
            // 点过赞了，取消点赞
            boolean success = this.update().setSql("liked = liked - 1").eq("id", blogCommentId).update();
            // 取消点赞成功，删除redis中保存的用户数据
            if (success){
                stringRedisTemplate.opsForSet().remove(RedisConstants.BLOG_COMMENT_LIKED_KEY+blogCommentId.toString(),user.getId().toString());
            }
        }
        return Result.ok();
    }

    /**
     * 判断登录用户是否点赞该评论
     * @param blogComment
     */
    public void isBlogCommentLiked(BlogComments blogComment){
        UserDTO user = UserHolder.getUser();
        if (user == null){
            return;
        }
        Boolean member = stringRedisTemplate.opsForSet().isMember(RedisConstants.BLOG_COMMENT_LIKED_KEY + blogComment.getId().toString(), user.getId().toString());
        blogComment.setIsLike(member);
    }
}
