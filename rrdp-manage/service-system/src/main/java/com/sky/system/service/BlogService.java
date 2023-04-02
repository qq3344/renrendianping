package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Blog;
import com.sky.model.vo.BlogVo;

/**

 * @version 1.0
 * @time 2023/3/8
 */
public interface BlogService extends IService<Blog> {
    /**
     * 分页查询博客日记
     * @param iPage 分页对象
     * @param nameQueryDTO 查询对象
     * @return
     */
    IPage<BlogVo> queryBlogList(IPage<BlogVo> iPage, NameQueryDTO nameQueryDTO);

    /**
     * 删除blog
     * @param id blogId
     */
    void removeBlogById(Integer id);

    /**
     * 根据id查询博客详情
     * @param id 博客id
     * @return
     */
    BlogVo queryBlogVoById(Integer id);
}
