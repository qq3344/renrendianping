package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Blog;
import com.sky.model.evaluate.BlogComments;
import com.sky.model.vo.BlogVo;
import com.sky.system.mapper.BlogMapper;
import com.sky.system.service.BlogCommentsService;
import com.sky.system.service.BlogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/8
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
        implements BlogService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private BlogCommentsService blogCommentsService;

    @Override
    public IPage<BlogVo> queryBlogList(IPage<BlogVo> iPage, NameQueryDTO nameQueryDTO) {
        return blogMapper.queryBlogList(iPage,nameQueryDTO);
    }

    /**
     * 删除blog
     * @param id blogId
     */
    @Override
    public void removeBlogById(Integer id) {
        // 删除blog之前先将blog下面的comments删除
        LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogComments::getBlogId,id);
        blogCommentsService.remove(queryWrapper);
        // 删除blog
        this.removeById(id);
    }

    /**
     * 根据id查询博客详情
     * @param id 博客id
     * @return
     */
    @Override
    public BlogVo queryBlogVoById(Integer id) {
        return blogMapper.queryBlogVoById(id);
    }
}
