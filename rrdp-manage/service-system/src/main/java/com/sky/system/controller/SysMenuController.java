package com.sky.system.controller;

import com.sky.common.result.Result;
import com.sky.model.system.SysMenu;
import com.sky.model.vo.AssignMenuVo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 * @version 1.0
 * @time 2022/12/10
 */
@RestController
@RequestMapping("/admin/system/sysMenu")
@Api(tags = "系统管理-菜单管理")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    @Log(title = "菜单管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.sysMenu.add')")
    @ApiOperation("添加菜单接口")
    @PostMapping("/saveSysMenu")
    public Result<String> saveSysMenu(@RequestBody SysMenu sysMenu){
        sysMenuService.save(sysMenu);
        return Result.ok();
    }

    @Log(title = "菜单管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysMenu.remove')")
    @ApiOperation("删除菜单接口")
    @DeleteMapping("/remove/{id}")
    public Result<String> removeSysMenu(
            @ApiParam(name = "id",value = "菜单id",required = true)
            @PathVariable("id") Long id){
        sysMenuService.removeSysMenu(id);
        return Result.ok();
    }


    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    @ApiOperation("查询菜单接口")
    @GetMapping("/queryById/{id}")
    public Result<SysMenu> queryById(@PathVariable("id") Long id){
        SysMenu sysMenu = sysMenuService.getById(id);
        return Result.ok(sysMenu);
    }

    @Log(title = "菜单管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.sysMenu.update')")
    @ApiOperation("修改菜单接口")
    @PutMapping("/updateSysMenu")
    public Result<String> updateSysMenu(@RequestBody SysMenu sysMenu){
        sysMenuService.updateById(sysMenu);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    @ApiOperation("树形菜单接口")
    @GetMapping("/treeMenu")
    public Result<List<SysMenu>> treeMenu(){
        List<SysMenu> sysMenuList = sysMenuService.treeMenu();
        return Result.ok(sysMenuList);
    }
    
    @ApiOperation("根据角色获取菜单")
    @GetMapping("/toAssign/{roleId}")
    public Result<List<SysMenu>> toAssign(@PathVariable("roleId") Long roleId){
        List<SysMenu> sysMenuList = sysMenuService.toAssign(roleId);
        return Result.ok(sysMenuList);
    }

    @ApiOperation("根据角色id获取菜单,返回所有的选中的MenuId")
    @GetMapping("/getSelectMenuId/{roleId}")
    public Result<List<Long>> getMenuId(@PathVariable("roleId") Long roleId){
        List<Long> selectMenuId = sysMenuService.getSelectMenuId(roleId);
        return Result.ok(selectMenuId);
    }

    @Log(title = "菜单管理",businessType = BusinessType.ASSGIN)
    @PreAuthorize("hasAuthority('bnt.sysRole.assignAuth')")
    @ApiOperation("给角色分配菜单")
    @PostMapping("/doAssign")
    public Result<String> doAssign(@RequestBody AssignMenuVo assignMenuVo){
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }

}
