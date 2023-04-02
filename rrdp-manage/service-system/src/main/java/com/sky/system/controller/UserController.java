package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.evaluate.UserInfo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.UserInfoService;
import com.sky.system.service.UserService;
import com.sky.model.evaluate.User;
import com.sky.model.vo.UserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/1
 */
@Api(tags = "点评管理-用户管理")
@RestController
@RequestMapping("/admin/evaluate/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页查询用户接口")
    @PreAuthorize("hasAuthority('bnt.user.list')")
    public Result<IPage<User>> pageQuery(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
            Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
            Integer limit,
            @ApiParam(name = "userQueryVo",value = "查询对象",required = false)
            UserQueryVo userQueryVo){
        IPage<User> page1 = new Page<>(page,limit);
        IPage<User> userPage = userService.pageQuery(page1,userQueryVo);
        return Result.ok(userPage);
    }


    @Log(title = "用户管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.user.add')")
    @ApiOperation("添加用户接口")
    @PostMapping("/save")
    public Result<String> saveUser(@RequestBody User user){
        userService.save(user);
        return Result.ok();
    }

    @Log(title = "用户管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.user.remove')")
    @ApiOperation("删除单个用户接口")
    @DeleteMapping("/remove/{id}")
    public Result<String> remove(@PathVariable("id") Long id){
        userService.removeById(id);
        return Result.ok();
    }

    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.user.update')")
    @ApiOperation("更新用户接口")
    @PutMapping("/update")
    public Result<String> updateById(@RequestBody User user){
        userService.updateById(user);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.user.list')")
    @ApiOperation("根据id查询用户信息详情")
    @GetMapping("/queryInfoById/{id}")
    public Result<UserInfo> queryInfoById(@PathVariable("id") Integer id){
        UserInfo userInfo = userInfoService.getById(id);
        return Result.ok(userInfo);
    }


}
