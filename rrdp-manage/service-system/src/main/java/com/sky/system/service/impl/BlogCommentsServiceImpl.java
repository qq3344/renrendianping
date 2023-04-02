package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.dto.CommentQueryDTO;
import com.sky.model.evaluate.BlogComments;
import com.sky.model.vo.BlogCommentsVo;
import com.sky.system.mapper.BlogCommentsMapper;
import com.sky.system.mapper.UserMapper;
import com.sky.system.service.BlogCommentsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments>
        implements BlogCommentsService {

    @Resource
    private BlogCommentsMapper blogCommentsMapper;

    @Resource
    private UserMapper userMapper;
    /**
     * 分页查询博客评论列表
     * @param iPage 分页对象
     * @param commentQueryDTO 查询对象
     * @return
     */
    @Override
    public IPage<BlogCommentsVo> queryBlogCommentsList(IPage<BlogCommentsVo> iPage, CommentQueryDTO commentQueryDTO) {
        IPage<BlogCommentsVo> blogCommentsVoIPage = blogCommentsMapper.queryBlogCommentsList(iPage,commentQueryDTO);
        blogCommentsVoIPage.getRecords().stream()
                .forEach(blogCommentsVo -> {
                    if (blogCommentsVo.getAnswerId() != 0){
                        blogCommentsVo.setPNickName(userMapper.selectById(blogCommentsVo.getAnswerId()).getNickName());
                    }else{
                        blogCommentsVo.setPNickName(null);
                    }
                });
        return blogCommentsVoIPage;
    }
}
