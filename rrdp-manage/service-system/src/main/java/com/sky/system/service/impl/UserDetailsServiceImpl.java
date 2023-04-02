package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.model.system.SysUser;
import com.sky.system.custom.CustomUser;
import com.sky.system.mapper.SysUserMapper;
import com.sky.system.service.SysMenuService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysMenuService sysMenuService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        //如果查询不到数据就通过抛出异常来给出提示
        if (Objects.isNull(sysUser)){
            throw new UsernameNotFoundException("用户名不存在！");
        }
        //查询用户账号状态，账号已停用
        if(sysUser.getStatus() == 0) {
            throw new RuntimeException("账号已停用");
        }
        // 获取用户按钮权限信息
        List<String> permsList = sysMenuService.queryButtonPermsList(sysUser.getId());
        // 将用户信息返回
        return new CustomUser(sysUser, permsList);
    }
}
