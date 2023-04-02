package com.sky.controller;


import com.sky.dto.Result;
import com.sky.pojo.BlogComments;
import com.sky.service.IBlogCommentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

    @Resource
    private IBlogCommentsService blogCommentsService;

    /**
     * 博客评论
     * @param blogComments
     * @return
     */
    @PostMapping("/saveBlogComment")
    public Result saveBlogComment(@RequestBody BlogComments blogComments){
        return blogCommentsService.saveBlogComment(blogComments);
    }

    /**
     * 展示所有评论
     * @param blogId
     * @return
     */
    @GetMapping("/showBlogComments/{id}")
    public Result showBlogComments(@PathVariable("id") Long blogId){
        return blogCommentsService.showBlogComments(blogId);
    }

    /**
     * 返回博客的总评论数
     * @param blogId
     * @return
     */
    @GetMapping("/getBlogCommentsTotal/{id}")
    public Result getBlogCommentsTotal(@PathVariable("id") Long blogId){
        return blogCommentsService.getBlogCommentsTotal(blogId);
    }

    /**
     * 删除评论
     * @param blogCommentId 评论id
     * @return
     */
    @DeleteMapping("/deleteComment/{id}")
    public Result deleteComment(@PathVariable("id") Long blogCommentId){
        return blogCommentsService.deleteComment(blogCommentId);
    }

    /**
     * 评论点赞或者取消点赞
     * @param blogCommentId
     * @return
     */
    @PutMapping("/like/{id}")
    public Result likeComment(@PathVariable("id") Long blogCommentId){
        return blogCommentsService.likeComment(blogCommentId);
    }

}
