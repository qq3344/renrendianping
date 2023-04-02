package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.system.SysLoginLog;
import com.sky.model.vo.SysLoginLogQueryVo;
import com.sky.system.service.SysLoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/2/7
 */
@Api(tags = "系统管理-登录日志管理")
@RestController
@RequestMapping("/admin/system/sysLoginLog")
public class SysLoginLogController {

    @Resource
    private SysLoginLogService sysLoginLogService;

    @PreAuthorize("hasAuthority('bnt.sysOperLog.list')")
    @ApiOperation("分页查询登录日记接口")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SysLoginLog>> pageQuery(
                                                @ApiParam(name = "page",value = "当前页码",required = true)
                                                @PathVariable("page") Integer page,
                                                @ApiParam(name = "limit",value = "每页记录数",required = true)
                                                @PathVariable("limit") Integer limit,
                                                @ApiParam(name = "sysLoginLogQueryVo",value = "查询对象",required = false)
                                                SysLoginLogQueryVo sysLoginLogQueryVo){
        IPage<SysLoginLog> sysLoginLogPage = new Page<>(page, limit);
        IPage<SysLoginLog> iPage = sysLoginLogService.pageQuery(sysLoginLogPage,sysLoginLogQueryVo);
        return Result.ok(iPage);
    }

    @ApiOperation("根据id查询登录日志接口")
    @GetMapping("/queryById/{id}")
    public Result<SysLoginLog> queryById(@PathVariable Long id){
        SysLoginLog sysLoginLog = sysLoginLogService.getById(id);
        return Result.ok(sysLoginLog);
    }

}
