package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.model.system.SysOperLog;
import com.sky.model.vo.SysOperLogQueryVo;


public interface SysOperLogService {


    /**
     * 保存操作日志
     * @param operLog 操作日志对象
     */
    void saveSysLog(SysOperLog operLog);


    /**
     * 分页查询操作日志
     * @param sysOperLogPage 分页对象
     * @param sysOperLogQueryVo  分页查询对象
     * @return
     */
    Page<SysOperLog> pageQuery(IPage<SysOperLog> sysOperLogPage, SysOperLogQueryVo sysOperLogQueryVo);

    /**
     * 根据id查询详情操作日志
     * @param id id
     * @return
     */
    SysOperLog queryById(Long id);
}
