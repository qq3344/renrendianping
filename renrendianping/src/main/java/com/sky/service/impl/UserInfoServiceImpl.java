package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.sky.dto.Result;
import com.sky.dto.UserDTO;
import com.sky.pojo.Follow;
import com.sky.pojo.User;
import com.sky.pojo.UserInfo;
import com.sky.mapper.UserInfoMapper;
import com.sky.service.IFollowService;
import com.sky.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.IUserService;
import com.sky.utils.RedisConstants;
import com.sky.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    private IFollowService followService;

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 个人信息查询
     * @param userId
     * @return
     */
    @Override
    public Result info(Long userId) {
        // 查询详情
        UserInfo info = getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    /**
     * 查询用户所有的粉丝
     * @param id
     * @return
     */
    @Override
    public Result queryFans(Long id) {
        // 先查询所有关注了userId的人的id
        List<Follow> followUserId = followService.query().eq("follow_user_id", id).list();
        if (followUserId == null || followUserId.isEmpty()){
            return Result.ok();
        }
        // 通过stream流取出所有的id
        List<Long> ids = followUserId.stream().map(Follow::getUserId).collect(Collectors.toList());
        // 在通过id 查询 userDTO
        List<User> userList = userService.listByIds(ids);
        // 这里只需要返回 userDTO
        List<UserDTO> userDTOList = userList.stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOList);
    }

    /**
     * 查询当前用户所有的关注
     * @param userId
     * @return
     */
    @Override
    public Result queryFollows(Long userId) {
        // 查询出所有的关注
        List<Follow> followList = followService.query().eq("user_id", userId).list();
        // 如果没有数据直接返回
        if (followList == null || followList.isEmpty()){
            return Result.ok();
        }
        // 获取所有关注的人的id
        List<Long> ids = followList.stream().map(Follow::getFollowUserId).collect(Collectors.toList());
        // 通过id查询用户信息
        List<User> userList = userService.listByIds(ids);
        // 将用户信息封装到UserDTO
        List<UserDTO> userDTOList = userList.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        // 返回
        return Result.ok(userDTOList);
    }

    /**
     * 修改昵称，根据userId
     * @param user
     * @param token
     * @return
     */
    @Override
    public Result updateNickName(User user, String token) {
        // 先删除redis中原来的值
        stringRedisTemplate.delete(RedisConstants.LOGIN_USER_KEY+token);
        boolean flag = userService.update().set("nick_name", user.getNickName()).eq("id", user.getId()).update();

        if (!flag){
            return Result.fail("服务器异常");
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        UserHolder.saveUser(userDTO);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((s, o) -> o.toString()));
        // 重新添加到redis
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,map);
        return Result.ok();
    }

    /**
     * 根据用户id查询个人介绍
     * @param userId
     * @return
     */
    @Override
    public Result queryIntroduceById(Long userId) {
        UserInfo userInfo = query().eq("user_id", userId).one();
        return Result.ok(userInfo.getIntroduce());
    }

    /**
     * 修改性别
     * @param userInfo
     * @return
     */
    @Override
    public Result updateGender(UserInfo userInfo) {
        update().set("gender", userInfo.getGender()).eq("user_id",userInfo.getUserId()).update();
        return Result.ok();
    }

    /**
     * 修改生日
     * @param userInfo
     * @return
     */
    @Override
    public Result updateBirthday(UserInfo userInfo) {
        update().set("birthday", userInfo.getBirthday()).eq("user_id",userInfo.getUserId()).update();
        return Result.ok();
    }

    /**
     * 修改地址
     * @param userInfo
     * @return
     */
    @Override
    public Result updateCity(UserInfo userInfo) {
        update().set("city", userInfo.getCity()).eq("user_id",userInfo.getUserId()).update();
        return Result.ok();
    }

    /**
     * 更新用户头像
     * @param token
     * @param filename
     * @param id
     */
    @Override
    public void updateAvatar(String token,String filename, Long id) {
        stringRedisTemplate.delete(RedisConstants.LOGIN_USER_KEY+token);
        userService.update().eq("id",id).set("icon","/imgs"+filename).update();
        UserDTO user = UserHolder.getUser();
        user.setIcon("/imgs"+filename);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        UserHolder.saveUser(userDTO);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((s, o) -> o.toString()));
        // 重新添加到redis
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,map);
    }

    /**
     * 修改个人介绍
     * @param userInfo
     * @return
     */
    @Override
    public Result updateIntroduce(UserInfo userInfo) {
        boolean flag = update().set("introduce", userInfo.getIntroduce()).eq("user_id", userInfo.getUserId()).update();
        if (!flag){
            return Result.fail("服务器异常");
        }
        return Result.ok();
    }




}
