package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.system.SysLoginLog;
import com.sky.model.vo.SysLoginLogQueryVo;

/**

 * @version 1.0
 * @time 2023/2/6
 */
public interface SysLoginLogService extends IService<SysLoginLog> {
    /**
     * 记录登录记录
     * @param username  用户名
     * @param status    状态
     * @param ipaddr    ip地址
     * @param message   消息内容
     */
    void recordLoginLog(String username, Integer status, String ipaddr, String message);

    /**
     * 分页查询登录记录
     * @param sysLoginLogPage  分页对象
     * @param sysLoginLogQueryVo   分页查询对象
     * @return
     */
    IPage<SysLoginLog> pageQuery(IPage<SysLoginLog> sysLoginLogPage, SysLoginLogQueryVo sysLoginLogQueryVo);
}
