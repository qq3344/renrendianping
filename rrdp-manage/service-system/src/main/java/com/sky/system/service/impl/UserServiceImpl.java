package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.system.mapper.UserMapper;
import com.sky.system.service.UserService;
import com.sky.model.evaluate.User;
import com.sky.model.vo.UserQueryVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/1
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 分页查询用户
     * @param page1 分页对象
     * @param userQueryVo 查询表单
     * @return
     */
    @Override
    public IPage<User> pageQuery(IPage<User> page1, UserQueryVo userQueryVo) {
        return userMapper.pageQuery(page1,userQueryVo);
    }
}
