package com.sky.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.evaluate.UserInfo;
import com.sky.system.mapper.UserInfoMapper;
import com.sky.system.service.UserInfoService;
import org.springframework.stereotype.Service;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
            implements UserInfoService {
}
