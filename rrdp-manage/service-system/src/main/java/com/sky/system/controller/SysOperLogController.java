package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.system.SysOperLog;
import com.sky.model.vo.SysOperLogQueryVo;
import com.sky.system.service.SysOperLogService;
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
 * @time 2023/2/8
 */
@RequestMapping("/admin/system/sysOperLog")
@RestController
@Api(tags = "系统管理-操作日志管理")
public class SysOperLogController {

    @Resource
    private SysOperLogService sysOperLogService;

//    @PreAuthorize("hasAuthority('bnt.sysOper.list')")
    @ApiOperation("操作日志分页查询你")
    @GetMapping("/{page}/{limit}")
    public Result<IPage<SysOperLog>> pageQuery(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable Integer limit,
            SysOperLogQueryVo sysOperLogQueryVo){
        IPage<SysOperLog> sysOperLogPage = new Page<>(page,limit);
        Page<SysOperLog> iPage = sysOperLogService.pageQuery(sysOperLogPage,sysOperLogQueryVo);
        return Result.ok(iPage);
    }

    @ApiOperation("根据id查询操作日志详情")
    @GetMapping("/queryById/{id}")
    public Result<SysOperLog> queryById(
            @ApiParam(name = "id",value = "id",required = true)
            @PathVariable Long id){
        SysOperLog sysOperLog = sysOperLogService.queryById(id);
        return Result.ok(sysOperLog);
    }

}
