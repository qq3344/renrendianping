package com.sky.system.controller;

import com.sky.common.result.Result;
import com.sky.common.util.JwtUtil;
import com.sky.model.vo.LoginVo;
import com.sky.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Slf4j
@Api(tags = "系统管理-登录管理")
@RequestMapping("/admin/system/index")
@RestController
public class IndexController {

    @Resource
    private SysUserService sysUserService;

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody LoginVo loginVo){
        return sysUserService.login(loginVo);
//        // 根据用户名判断是否存在该用户
//        SysUser sysUser = sysUserService.queryByUsername(loginVo.getUsername());
//        if (Objects.isNull(sysUser)){
//            throw new MyException(ResultCodeEnum.ACCOUNT_ERROR);
//        }
//        // 校验密码
//        if (!DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes()).equals(sysUser.getPassword())){
//            throw new MyException(ResultCodeEnum.PASSWORD_ERROR);
//        }
//        // 判断账号状态
//        if (sysUser.getStatus().equals(SystemConstants.STOP)){
//            throw new MyException(ResultCodeEnum.ACCOUNT_STOP);
//        }

    }

    @ApiOperation("获取登录用户信息")
    @GetMapping("/info")
    public Result<Map<String,Object>> info(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String username = JwtUtil.getUsername(token);
        Map<String, Object> map = sysUserService.getUserInfo(username);
        return Result.ok(map);
    }

    @ApiOperation("退出接口")
    @PostMapping("/logout")
    public Result<String> logout(){
        SecurityContextHolder.clearContext();
        return Result.ok();
    }

}
