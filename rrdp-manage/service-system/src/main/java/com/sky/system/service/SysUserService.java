package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.common.result.Result;
import com.sky.model.system.SysUser;
import com.sky.model.vo.LoginVo;
import com.sky.model.vo.SysUserQueryVo;

import java.util.Map;

/**

 * @version 1.0
 * @time 2022/12/9
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 分页查询用户信息
     * @param userPage
     * @param sysUserQueryVo
     * @return
     */
    IPage<SysUser> pageQuery(IPage<SysUser> userPage, SysUserQueryVo sysUserQueryVo);

    /**
     * 修改用户状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUser queryByUsername(String username);

    /**
     * 获取当前登录用户的信息
     * @param username
     * @return
     */
    Map<String, Object> getUserInfo(String username);

    /**
     * 登录逻辑校验
     * @param loginVo
     * @return
     */
    Result<Map<String, Object>> login(LoginVo loginVo);
}
