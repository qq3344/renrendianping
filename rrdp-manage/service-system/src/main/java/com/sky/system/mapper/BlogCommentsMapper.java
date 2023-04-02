package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.dto.CommentQueryDTO;
import com.sky.model.evaluate.BlogComments;
import com.sky.model.vo.BlogCommentsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/8
 */
@Mapper
public interface BlogCommentsMapper extends BaseMapper<BlogComments> {
    /**
     * 分页查询博客评论列表
     * @param iPage 分页对象
     * @param commentQueryDTO 查询对象
     * @return
     */
    IPage<BlogCommentsVo> queryBlogCommentsList(IPage<BlogCommentsVo> iPage,@Param("vo") CommentQueryDTO commentQueryDTO);
}
