package com.sky.common.util;


import com.sky.common.constant.CacheConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;


@Slf4j
public class JwtUtil {

    /**
     * 使用HS512加密算法 签名
     */
    static SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * 设置token过期时间
     */
    private static final long EXPIRE_TIME = 1000 * 20 * 60;

    /**
     * 创建token
     *
     * @param userId
     * @param username
     * @return
     */
    public static String createToken(Long userId, String username) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId.toString());
        map.put("username",username);
        String token = Jwts.builder()
                .setClaims(map)
                // 可以将基本不重要的对象信息放到claims
                .signWith(key)
                .setSubject("AUTH-USER")
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .compact();
        return token;
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public static Claims parseToken(String token) {
        //顺带说一句，当Jwt设置了有效期，有效期时间过了之后也会抛出异常，解决办法是try
        //catch一下，将异常抛给统一异常处理类
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户id
     *
     * @param token
     * @return
     */
    public static String getUserId(String token) {
        try {
            if (!StringUtils.hasText(token)){
                return null;
            }
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户名
     *
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        try {
            if (!StringUtils.hasText(token)){
                return null;
            }
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().get("username");
        } catch (Exception e) {
            return null;
        }    }

    /**
     * 判断是否过期
     *
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

    public static String getTokenKey(String userId) {
        return CacheConstants.LOGIN_TOKEN_KEY + userId;
    }

    public static void main(String[] args) {
        String token = JwtUtil.createToken(1L, "test");
//        token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInN1YiI6IkFVVEgtVVNFUiIsImV4cCI6MTY3NTMxODg5N30.Kf7lzRP3I9_zcVez-PZoxlVHWGWU8D7IfzcajMhjoFL1YY12-Jv8uowfiPe1E3-98MHw06h5efrtZle9CwqAeA";
//        token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInN1YiI6IkFVVEgtVVNFUiIsImV4cCI6MTY3NTMxOTQ5NX0.6btdwiF3_FZ-_WG9AVlc6GGuB6KaisAIH25LJZvm6yjrniaWOP9bMD-wABTqmYHsrGx6zrtAZXhF-JLUY7ffbg";
        System.out.println(token);

        String subject = JwtUtil.parseToken(token).getSubject();
        System.out.println(subject);


//        System.out.println(JwtUtil.parseToken(token).get("userId"));

        String userId = JwtUtil.getUserId(token);
        System.out.println(userId);

        String username = JwtUtil.getUsername(token);
        System.out.println(username);
    }

}
