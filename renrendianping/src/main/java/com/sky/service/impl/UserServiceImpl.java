package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.LoginFormDTO;
import com.sky.dto.Result;
import com.sky.dto.UserDTO;
import com.sky.pojo.User;
import com.sky.mapper.UserMapper;
import com.sky.pojo.UserInfo;
import com.sky.service.IUserInfoService;
import com.sky.service.IUserService;
import com.sky.utils.RedisConstants;
import com.sky.utils.RegexUtils;
import com.sky.utils.SystemConstants;
import com.sky.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service("userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 发送短信验证码并保存验证码
     * @param phone
     * @param session
     * @return
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (!phone.matches("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$")){
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号码输入有误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4.保存验证码到session(弃用)
        // session.setAttribute("code",code);

        // 4.保存验证码到redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY+phone,code,RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 5.发送验证码 TODO 后期切换为aliyun
        log.error("发送验证码成功！验证码是："+code);
        // 6.返回ok
        return Result.ok("发送验证码成功！验证码是："+code);
    }

    /**
     * 登录功能
     * @param loginForm
     * @param session
     * @return
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        if(RegexUtils.isPhoneInvalid(loginForm.getPhone())){
            return Result.fail("您输入的手机号码格式错误！");
        }
        // 2.校验验证码，获取session中的验证码与表单提交的验证码进行比对(弃用)
//        String cacheCode = (String) session.getAttribute("code");

        // 2.校验验证码，从redis中的验证码与表单提交的验证码进行比对
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + loginForm.getPhone());
        if (cacheCode == null || loginForm.getCode() == null || !cacheCode.equals(loginForm.getCode())){
            return Result.fail("验证码输入有误!");
        }
        // 3.校验成功，判断手机号是否为新用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,loginForm.getPhone());
        // 3.1 新用户添加到数据库
        User user = this.getOne(queryWrapper);
        if (Objects.isNull(user)){
            // 添加用户基础信息到user表中
            user = createUserWithPhone(loginForm.getPhone());
            // 添加用户信息到userinfo中
            createUserInfo(user.getId());
        }
        // 3.2 将用户信息保存到session中(弃用)
//        session.setAttribute("user",userDTO);

        // 3.2 将用户信息保存到redis中
        // 3.2.1 随机生成一个token登录令牌
        String token = UUID.randomUUID().toString(true);
        // 3.2.2 将user对象转为hash存储
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);
        // 可以使用hutool工具，但是我没自己写过bean转map想自己体验一下
        // 这里还有一个坑就是Long=>String报错
        Map<String, Object> map = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((s, o) -> o.toString()));
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,map);
        // 设置过期时间
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token,RedisConstants.LOGIN_USER_TTL,TimeUnit.MINUTES);
        // 3.2.3 返回token
        return Result.ok(token);
    }




    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    @Override
    public Result queryUserById(Long userId) {
        // 查询详情
        User user = getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    /**
     * 用户签到
     * @return
     */
    @Override
    public Result sign() {
        // 获取当前用户信息
        Long userId = UserHolder.getUser().getId();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        String yearAndMonth = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 拼接
        String key = RedisConstants.USER_SIGN_KEY+userId+yearAndMonth;
        // 获取当前是这个月的第几天
        int day = now.getDayOfMonth() - 1;
        boolean b = stringRedisTemplate.opsForValue().setBit(key,day,true);
        return Result.ok(b);
    }

    /**
     * 统计连续签到数
     * @return
     */
    @Override
    public Result signCount() {
        // 获取当前用户信息
        Long userId = UserHolder.getUser().getId();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        String yearAndMonth = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 拼接
        String key = RedisConstants.USER_SIGN_KEY+userId+yearAndMonth;
        // 获取当前日期的  天
        int day = now.getDayOfMonth();
        // 获取今天位置所有的签到记录
        List<Long> list = stringRedisTemplate.opsForValue().bitField(key,
                BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(day)).valueAt(0));
        if (list == null || list.isEmpty()){
            // 没有签到记录
            return Result.ok(0);
        }
        Long number = list.get(0);
        if (number == null || number == 0){
            return Result.ok(0);
        }
        int count = 0;
        while (true){
           if( (number & 1) == 0){
                break;
           }else{
                count ++;
           }
           number >>>= 1;
        }
        return Result.ok(count);
    }


    /**
     * 将新用户添加到数据库中
     * @param phone
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public User createUserWithPhone(String phone) {
        // 1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX +RandomUtil.randomString(10));
        // 2.保存到数据库
        this.save(user);
        return user;
    }

    /**
     * 添加用户信息到userinfo
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void createUserInfo(Long userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfoService.save(userInfo);
    }

    @Override
    public Result isSign() {
        // 获取当前用户信息
        Long userId = UserHolder.getUser().getId();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        String yearAndMonth = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        // 拼接
        String key = RedisConstants.USER_SIGN_KEY+userId+yearAndMonth;
        // 获取当前是这个月的第几天
        int day = now.getDayOfMonth() - 1;
        return Result.ok(stringRedisTemplate.opsForValue().getBit(key, day));
    }
}
