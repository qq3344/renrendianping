package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.dto.CommentQueryDTO;
import com.sky.model.evaluate.BlogComments;
import com.sky.model.vo.BlogCommentsVo;


public interface BlogCommentsService extends IService<BlogComments> {
    /**
     * 分页查询博客评论列表
     * @param iPage 分页对象
     * @param commentQueryDTO 查询对象
     * @return
     */
    IPage<BlogCommentsVo> queryBlogCommentsList(IPage<BlogCommentsVo> iPage, CommentQueryDTO commentQueryDTO);
}
