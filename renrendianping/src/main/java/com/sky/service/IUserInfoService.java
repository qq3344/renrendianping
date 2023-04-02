package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.User;
import com.sky.pojo.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;


public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 个人信息查询
     * @param userId
     * @return
     */
    Result info(Long userId);

    /**
     * 查询用户所有的粉丝
     * @param id
     * @return
     */
    Result queryFans(Long id);

    /**
     * 查询当前用户所有的关注
     * @param userId
     * @return
     */
    Result queryFollows(Long userId);

    /**
     * 修改昵称，根据userId
     * @param user
     * @param token
     * @return
     */
    Result updateNickName(User user, String token);

    /**
     * 修改个人介绍，根据userId
     * @param userInfo
     * @return
     */
    Result updateIntroduce(UserInfo userInfo);

    /**
     * 根据用户id查询个人介绍
     * @param userId
     * @return
     */
    Result queryIntroduceById(Long userId);

    /**
     * 修改性别
     * @param userInfo
     * @return
     */
    Result updateGender(UserInfo userInfo);

    /**
     * 修改生日
     * @param userInfo
     * @return
     */
    Result updateBirthday(UserInfo userInfo);

    /**
     * 修改地址
     * @param userInfo
     * @return
     */
    Result updateCity(UserInfo userInfo);

    /**
     * 更新用户头像
     * @param token
     * @param filename
     * @param id
     */
    void updateAvatar(String token,String filename, Long id);
}
