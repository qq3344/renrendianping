package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.model.system.SysOperLog;
import com.sky.model.vo.SysOperLogQueryVo;
import com.sky.system.mapper.SysOperLogMapper;
import com.sky.system.service.SysOperLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    /**
     * 保存操作日志
     * @param operLog 操作日志对象
     */
    @Override
    public void saveSysLog(SysOperLog operLog) {
        sysOperLogMapper.insert(operLog);
    }

    /**
     * 分页查询操作日志
     * @param sysOperLogPage 分页对象
     * @param sysOperLogQueryVo  分页查询对象
     * @return
     */
    @Override
    public Page<SysOperLog> pageQuery(IPage<SysOperLog> sysOperLogPage, SysOperLogQueryVo sysOperLogQueryVo) {
        return sysOperLogMapper.pageQuery(sysOperLogPage,sysOperLogQueryVo);
    }

    /**
     * 根据id查询详情操作日志
     * @param id id
     * @return
     */
    @Override
    public SysOperLog queryById(Long id) {
        return sysOperLogMapper.selectById(id);
    }
}
