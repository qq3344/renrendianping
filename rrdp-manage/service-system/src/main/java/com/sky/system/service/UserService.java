package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.evaluate.User;
import com.sky.model.vo.UserQueryVo;

/**

 * @version 1.0
 * @time 2023/3/1
 */
public interface UserService extends IService<User> {
    /**
     * 分页查询用户
     * @param page1 分页对象
     * @param userQueryVo 查询表单
     * @return
     */
    IPage<User> pageQuery(IPage<User> page1, UserQueryVo userQueryVo);
}
