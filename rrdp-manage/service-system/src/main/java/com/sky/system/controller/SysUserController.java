package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.common.util.MD5;
import com.sky.model.system.SysUser;
import com.sky.model.vo.SysUserQueryVo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2022/12/9
 */
@Api(tags = "系统管理-用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Log(title = "用户管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.sysUser.add')")
    @ApiOperation("添加用户接口")
    @PostMapping("/saveSysUser")
    public Result<String> saveSysUser(@RequestBody SysUser sysUser) {
        String newPassword = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(newPassword);
        sysUserService.save(sysUser);
        return Result.ok();
    }

    @Log(title = "用户管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysUser.remove')")
    @ApiOperation("删除用户接口")
    @DeleteMapping("/remove/{id}")
    public Result<String> removeSysUser(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("查询单个用户接口")
    @GetMapping("/queryById/{id}")
    public Result<SysUser> queryById(@PathVariable Long id) {
        SysUser sysUser = sysUserService.getById(id);
        return Result.ok(sysUser);
    }

    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation("修改用户接口")
    @PutMapping("/updateSysUser")
    public Result<String> updateSysUser(@RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation("分页查询用户接口")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SysUser>> pageQuery(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "sysUserQueryVo", value = "查询对象", required = false)
                    SysUserQueryVo sysUserQueryVo) {
        IPage<SysUser> userPage = new Page<>(page, limit);
        IPage<SysUser> iPage = sysUserService.pageQuery(userPage, sysUserQueryVo);
        return Result.ok(iPage);
    }

    @Log(title = "用户管理",businessType = BusinessType.STATUS)
    @ApiOperation("修改用户状态接口")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result<String> updateStatus(
            @ApiParam(name = "id", value = "用户id", required = true)
            @PathVariable Long id,
            @ApiParam(name = "status", value = "状态码", required = true)
            @PathVariable Integer status) {
        sysUserService.updateStatus(id,status);
        return Result.ok();
    }

}
