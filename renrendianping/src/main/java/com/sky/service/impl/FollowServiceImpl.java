package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.Result;
import com.sky.dto.UserDTO;
import com.sky.pojo.Follow;
import com.sky.mapper.FollowMapper;
import com.sky.pojo.User;
import com.sky.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.IUserInfoService;
import com.sky.service.IUserService;
import com.sky.utils.RedisConstants;
import com.sky.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 关注或者取消关注
     * @param followUserId 关注者id
     * @param isFollow  关注还是取消关注
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result follow(Long followUserId, Boolean isFollow) {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 判断关注还是取消关注
        if (isFollow) {
            // 关注，新增
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean success = save(follow);
            // 关注后，当前登录用户关注+1
            userInfoService.update().setSql("followee = followee + 1").eq("user_id",userId).update();
            // 被关注的用户粉丝数+1
            userInfoService.update().setSql("fans = fans + 1").eq("user_id",followUserId).update();
            if (success){
                // 将关注者的id存入redis ，key为follows:用户id
                stringRedisTemplate.opsForSet().add(RedisConstants.FOLLOW_KEY+userId,followUserId.toString());
            }
        }else{
            // 取消关注，删除
            LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Follow::getUserId,userId)
                    .eq(Follow::getFollowUserId,followUserId);
            boolean success = remove(queryWrapper);
            // 取消关注后，当前登录用户关注-1
            userInfoService.update().setSql("followee = followee - 1").eq("user_id",userId).update();
            // 被取消关注的用户粉丝数-1
            userInfoService.update().setSql("fans = fans - 1").eq("user_id",followUserId).update();
            if (success) {
                stringRedisTemplate.opsForSet().remove(RedisConstants.FOLLOW_KEY + userId, followUserId.toString());
            }
        }
        return Result.ok();
    }

    /**
     * 是否关注
     * @param followUserId 关注者的id
     * @return
     */
    @Override
    public Result isFollow(Long followUserId) {
        Long userId = UserHolder.getUser().getId();
        Follow follow = query().eq("follow_user_id", followUserId).eq("user_id", userId).one();
        return Result.ok(follow != null);
    }

    /**
     * 共同关注
     * @param id  查看的人的id
     * @return
     */
    @Override
    public Result followCommons(Long id) {
        // 获取当前用户id
        Long userId = UserHolder.getUser().getId();
        // 当前用户redis+id
        String key = RedisConstants.FOLLOW_KEY+userId;
        // 查询的用户的key
        String key2 = RedisConstants.FOLLOW_KEY + id;
        // 共同关注
        Set<String> set = stringRedisTemplate.opsForSet().intersect(key, key2);
        // 如果没有，直接返回空数组
        if (set == null || set.isEmpty()){
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = set.stream().map(Long::valueOf).collect(Collectors.toList());
        List<UserDTO> userDTOList = userService.listByIds(ids)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOList);
    }
}
