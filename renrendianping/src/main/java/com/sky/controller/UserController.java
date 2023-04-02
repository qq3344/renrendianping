package com.sky.controller;


import cn.hutool.core.bean.BeanUtil;
import com.sky.dto.LoginFormDTO;
import com.sky.dto.Result;
import com.sky.dto.UserDTO;
import com.sky.pojo.User;
import com.sky.pojo.UserInfo;
import com.sky.service.IUserInfoService;
import com.sky.service.IUserService;
import com.sky.utils.RedisConstants;
import com.sky.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

//    @Resource
//    private StringRedisTemplate redisTemplate;
    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 发送短信验证码并保存验证码
        return userService.sendCode(phone,session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        // 实现登录功能
        return userService.login(loginForm,session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(String token){
        // TODO 实现登出功能
//        redisTemplate.opsForHash().delete(RedisConstants.LOGIN_USER_KEY + token);
        return Result.ok();
    }

    /**
     * 获取登录用户的信息
     * @return
     */
    @GetMapping("/me")
    public Result me(){
        // 获取当前登录的用户并返回
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    /**
     * 个人信息查询
     * @param userId
     * @return
     */
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        return userInfoService.info(userId);
    }

    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId){
        return userService.queryUserById(userId);
    }



    /**
     * 用户签到接口
     * @return
     */
    @PostMapping("/sign")
    public Result sign(){
        return userService.sign();
    }

    /**
     * 统计连续签到数
     * @return
     */
    @GetMapping("/sign/count")
    public Result signCount(){
        return userService.signCount();
    }

    /**
     * 获取今天是否已经签到
     * @return
     */
    @GetMapping("/sign")
    public Result isSign(){
        return userService.isSign();
    }
}
