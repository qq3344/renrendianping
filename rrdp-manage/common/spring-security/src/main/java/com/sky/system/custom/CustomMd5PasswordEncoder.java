package com.sky.system.custom;

import com.sky.common.util.MD5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义security密码校验

 * @version 1.0
 * @time 2023/1/31
 */
@Slf4j
public class CustomMd5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        // 进行一个md5加密
        return MD5.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 通过md5校验
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
