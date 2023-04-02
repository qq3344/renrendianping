package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Blog;
import com.sky.model.vo.BlogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/8
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 分页查询blog
     * @param iPage 分页对象
     * @param nameQueryDTO 查询对象
     * @return
     */
    IPage<BlogVo> queryBlogList(IPage<BlogVo> iPage, @Param("vo") NameQueryDTO nameQueryDTO);

    /**
     * 根据id查询博客详情
     * @param id
     * @return
     */
    BlogVo queryBlogVoById(Integer id);
}
