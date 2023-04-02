package com.sky.controller;

import com.sky.dto.Result;
import com.sky.pojo.User;
import com.sky.pojo.UserInfo;
import com.sky.service.IUserInfoService;
import com.sky.utils.SystemConstants;
import com.sky.utils.UploadUtils;
import com.sky.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询当前用户所有的粉丝
     * @param id
     * @return
     */
    @GetMapping("/queryFans/{id}")
    public Result queryFans(@PathVariable("id") Long id){
        return userInfoService.queryFans(id);
    }

    /**
     * 查询当前用户所有的关注
     * @param userId
     * @return
     */
    @GetMapping("/queryFollows/{id}")
    public Result queryFollows(@PathVariable("id") Long userId){
        return userInfoService.queryFollows(userId);
    }

    /**
     * 修改昵称接口
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/updateNickName")
    public Result updateNickName(@RequestBody User user, HttpServletRequest request){
        // 删除redis中的key
        String token = request.getHeader("authorization");
        return userInfoService.updateNickName(user,token);
    }

    /**
     * 根据用户id查询个人介绍
     * @param userId
     * @return
     */
    @GetMapping("/queryIntroduceById/{id}")
    public Result queryIntroduceById(@PathVariable("id") Long userId){
        return userInfoService.queryIntroduceById(userId);
    }

    /**
     * 修改个人介绍，根据userId
     * @param userInfo
     * @return
     */
    @PostMapping("/updateIntroduce")
    public Result updateIntroduce(@RequestBody UserInfo userInfo){
        return userInfoService.updateIntroduce(userInfo);
    }

    /**
     * 修改性别
     * @param userInfo
     * @return
     */
    @PostMapping("/updateGender")
    public Result updateGender(@RequestBody UserInfo userInfo){
        return userInfoService.updateGender(userInfo);
    }

    /**
     * 修改生日
     * @param userInfo
     * @return
     */
    @PostMapping("/updateBirthday")
    public Result updateBirthday(@RequestBody UserInfo userInfo){
        return userInfoService.updateBirthday(userInfo);
    }

    /**
     * 修改地址
     * @param userInfo
     * @return
     */
    @PostMapping("/updateCity")
    public Result updateCity(@RequestBody UserInfo userInfo){
        return userInfoService.updateCity(userInfo);
    }

    @PostMapping("/uploadAvatar")
    public Result updateAvatar(@RequestParam("file") MultipartFile file,HttpServletRequest request){
        try {
            // 获取token，删除redis中的数据
            String token = request.getHeader("authorization");
            // 获取文件原本的名字
            String originalFilename = file.getOriginalFilename();
            // 新的文件名称，包含路径
            String filename = UploadUtils.createNewFileName("icons", originalFilename);
            userInfoService.updateAvatar(token,filename, UserHolder.getUser().getId());
            file.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR,filename));
            return Result.ok(filename);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
