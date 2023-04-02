package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IBlogService extends IService<Blog> {

    /**
     * 根据博客id查询详情
     * @param id 博客id
     * @return
     */
    Result queryBlogById(Long id);

    /**
     * 分页查询首页博客
     * @param current
     * @return
     */
    Result queryHotBlog(Integer current);

    /**
     * 博客点赞
     * @param id
     * @return
     */
    Result likeBlog(Long id);

    /**
     * 点赞top5
     * @param id
     * @return
     */
    Result queryBlogLikes(Long id);

    /**
     * 发布日记
     * @param blog
     * @return
     */
    Result saveBlog(Blog blog);

    /**
     * 分页查询关注的人的博客
     * @param max
     * @param offset
     * @return
     */
    Result queryBlogOfFollow(Long max, Integer offset);
}
