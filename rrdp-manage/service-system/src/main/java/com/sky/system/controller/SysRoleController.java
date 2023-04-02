package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.system.SysRole;
import com.sky.model.vo.AssignRoleVo;
import com.sky.model.vo.SysRoleQueryVo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**

 * @version 1.0
 * @time 2022/12/6
 */
@Api(tags = "系统管理-角色管理")
@RequestMapping("/admin/system/sysRole")
@RestController
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("查询所有角色信息接口")
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll() {
        return Result.ok(sysRoleService.list());
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("分页查询角色信息接口")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SysRole>> pageQuery(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "当前记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "roleQueryVo", value = "查询对象", required = false)
            SysRoleQueryVo sysRoleQueryVo) {
        IPage<SysRole> iPage = new Page<>(page, limit);
        IPage<SysRole> pageModel = sysRoleService.pageQuery(iPage,sysRoleQueryVo);
        return Result.ok(pageModel);
    }

    @Log(title = "角色管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加系统角色接口")
    @PostMapping("/save")
    public Result<String> saveSysRole(@RequestBody SysRole sysRole){
        sysRoleService.save(sysRole);
        return Result.ok();
    }

    @Log(title = "角色管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("删除单个系统角色接口")
    @DeleteMapping("/remove/{id}")
    public Result<String> remove(@PathVariable("id") Long id){
        sysRoleService.removeById(id);
        return Result.ok();
    }

    @Log(title = "角色管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("删除多个系统角色接口")
    @DeleteMapping("/batchRemove")
    public Result<String> batchRemove(@RequestBody List<Long> ids){
        sysRoleService.removeBatchByIds(ids);
        return Result.ok();
    }

    @ApiOperation("根据id查询系统角色接口")
    @GetMapping("/queryById/{id}")
    public Result<SysRole> queryById(@PathVariable("id") Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    @Log(title = "角色管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("更新系统角色接口")
    @PutMapping("/update")
    public Result<String> updateById(@RequestBody SysRole sysRole){
        sysRoleService.updateById(sysRole);
        return Result.ok();
    }

    @ApiOperation("获取用户角色信息接口")
    @GetMapping("/toAssign/{userId}")
    public Result<Map<String,Object>> toAssign(@PathVariable("userId") Long userId){
        Map<String,Object> roleMap = sysRoleService.toAssign(userId);
        return Result.ok(roleMap);
    }

    @Log(title = "角色管理",businessType = BusinessType.ASSGIN)
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("分配用户角色信息接口")
    @PostMapping("/doAssign")
    public Result<Map<String,Object>> toAssign(@RequestBody AssignRoleVo assignRoleVo){
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }

}
