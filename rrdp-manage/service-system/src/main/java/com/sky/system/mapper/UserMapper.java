package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.evaluate.User;
import com.sky.model.vo.UserQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/1
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 分页查询用户
     * @param page1 分页对象
     * @param userQueryVo 查询表单
     * @return
     */
    IPage<User> pageQuery(IPage<User> page1,@Param("vo") UserQueryVo userQueryVo);
}
