package com.sky.system.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.sky.common.constant.SystemConstants;
import com.sky.common.util.JwtUtil;
import com.sky.system.utils.RedisCache;
import com.sky.system.custom.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**

 * @version 1.0
 * @time 2023/1/31
 */
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的token
        String token = request.getHeader(SystemConstants.TOKEN);
        // 没有token直接放行，进入security的路径匹配是否需要认证才能访问
        if (!StringUtils.hasText(token)){
            filterChain.doFilter(request,response);
            return;
        }
        // 如果携带token访问需要进行解析,从redis中获取权限信息
        String userId = JwtUtil.getUserId(token);
        // 放行
        if (Objects.isNull(userId)){
            filterChain.doFilter(request,response);
            return;
        }
        CustomUser customUser = JSONUtil.toBean(stringRedisTemplate.opsForValue().get(JwtUtil.getTokenKey(userId)),CustomUser.class);
        // 从redis中获取权限
        List<SimpleGrantedAuthority> authorities = customUser.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 存入SecurityContextHolder
        // 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(customUser,null,authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
