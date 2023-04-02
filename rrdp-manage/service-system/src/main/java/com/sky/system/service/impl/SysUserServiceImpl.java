package com.sky.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.common.result.Result;
import com.sky.common.util.IpUtil;
import com.sky.common.util.JwtUtil;
import com.sky.model.system.SysUser;
import com.sky.model.vo.LoginVo;
import com.sky.model.vo.RouterVo;
import com.sky.model.vo.SysUserQueryVo;
import com.sky.system.custom.CustomUser;
import com.sky.system.mapper.SysUserMapper;
import com.sky.system.service.SysLoginLogService;
import com.sky.system.service.SysMenuService;
import com.sky.system.service.SysUserService;
import com.sky.system.utils.RedisCache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisCache redisCache;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysLoginLogService sysLoginLogService;

    /**
     * 通过AuthenticationManager的authenticate方法来进行用户认证,
     */
    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * 分页查询用户信息
     * @param userPage
     * @param sysUserQueryVo
     * @return
     */
    @Override
    public IPage<SysUser> pageQuery(IPage<SysUser> userPage, SysUserQueryVo sysUserQueryVo) {
        return baseMapper.pageQuery(userPage,sysUserQueryVo);
    }

    /**
     * 修改用户状态
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = this.getById(id);
        sysUser.setStatus(status);
        this.updateById(sysUser);
    }

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public SysUser queryByUsername(String username) {
        SysUser sysUser = this.query().eq("username", username).one();
        return sysUser;
    }

    /**
     * 获取当前登录用户的信息
     * @param username
     * @return
     */
    @Override
    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> map = new HashMap<>();
        SysUser sysUser = this.queryByUsername(username);
        //根据用户id获取菜单权限值
        List<RouterVo> routerVoList = sysMenuService.getUserMenuList(sysUser.getId());
        //根据用户id获取用户按钮权限
        List<String> permsList = sysMenuService.queryButtonPermsList(sysUser.getId());
        // 暂时还用不到
        map.put("roles",new HashSet<>());
        map.put("name",username);
        map.put("buttons", permsList);
        map.put("routers", routerVoList);
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return map;
    }

    @Override
    public Result<Map<String, Object>> login(LoginVo loginVo) {
        // 将表单数据封装到 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
        // authenticate方法会调用loadUserByUsername
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        // 校验成功，强转对象
        CustomUser customUser = (CustomUser) authenticate.getPrincipal();
        SysUser sysUser = customUser.getSysUser();
        // 添加登录日志到数据库
        sysLoginLogService.recordLoginLog(sysUser.getUsername(),1,
                IpUtil.getIpAddress(((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest()),
                "登录成功！");
        // redis 保存 用户权限信息
        stringRedisTemplate.opsForValue().set(JwtUtil.getTokenKey(sysUser.getId().toString()), JSON.toJSONString(customUser),30, TimeUnit.MINUTES);
        // 校验通过返回token
        String token = JwtUtil.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
}
