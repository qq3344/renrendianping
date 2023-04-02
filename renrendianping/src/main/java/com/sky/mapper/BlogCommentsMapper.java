package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.pojo.BlogComments;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogCommentsMapper extends BaseMapper<BlogComments> {

    /**
     * 查询所有的评论信息
     * @param blogId
     * @return
     */
    List<BlogComments> findCommentDetail(Long blogId);
}
