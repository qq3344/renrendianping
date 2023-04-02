package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.BlogComments;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IBlogCommentsService extends IService<BlogComments> {

    /**
     * 博客评论
     * @param blogComments
     * @return
     */
    Result saveBlogComment(BlogComments blogComments);

    /**
     * 展示所有的评论
     * @param blogId
     * @return
     */
    Result showBlogComments(Long blogId);

    /**
     * 返回博客的总评论数
     * @param blogId
     * @return
     */
    Result getBlogCommentsTotal(Long blogId);

    /**
     * 删除评论
     * @param blogCommentId
     * @return
     */
    Result deleteComment(Long blogCommentId);

    /**
     * 评论点赞或者取消点赞
     * @param blogCommentId
     * @return
     */
    Result likeComment(Long blogCommentId);
}
