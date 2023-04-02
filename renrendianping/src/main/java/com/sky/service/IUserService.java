package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.LoginFormDTO;
import com.sky.dto.Result;
import com.sky.pojo.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    /**
     * 发送短信验证码并保存验证码
     * @param phone
     * @param session
     * @return
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 登录功能
     * @param loginForm
     * @param session
     * @return
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    Result queryUserById(Long userId);

    /**
     * 用户签到
     * @return
     */
    Result sign();

    /**
     * 统计连续签到数
     * @return
     */
    Result signCount();

    Result isSign();
}
