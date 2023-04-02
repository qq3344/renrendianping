package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.system.SysLoginLog;
import com.sky.model.vo.SysLoginLogQueryVo;
import com.sky.system.mapper.SysLoginLogMapper;
import com.sky.system.service.SysLoginLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog>
        implements SysLoginLogService {

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    /**
     * 记录登录记录
     * @param username  用户名
     * @param status    状态
     * @param ipaddr    ip地址
     * @param message   消息内容
     */
    @Override
    public void recordLoginLog(String username, Integer status, String ipaddr, String message) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUsername(username);
        sysLoginLog.setStatus(status);
        sysLoginLog.setIpaddr(ipaddr);
        sysLoginLog.setMsg(message);
        // 保存到数据库
        this.save(sysLoginLog);
    }

    /**
     * 分页查询登录日志
     * @param sysLoginLogPage  分页对象
     * @param sysLoginLogQueryVo   分页查询对象
     * @return
     */
    @Override
    public IPage<SysLoginLog> pageQuery(IPage<SysLoginLog> sysLoginLogPage, SysLoginLogQueryVo sysLoginLogQueryVo) {
        IPage<SysLoginLog> page = sysLoginLogMapper.pageQuery(sysLoginLogPage,sysLoginLogQueryVo);
        return page;
    }
}
