package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.system.SysLoginLog;
import com.sky.model.vo.SysLoginLogQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/2/6
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
    /**
     * 分页查询登录日志
     * @param sysLoginLogPage  分页对象
     * @param sysLoginLogQueryVo  分页查询对象
     * @return
     */
    IPage<SysLoginLog> pageQuery(IPage<SysLoginLog> sysLoginLogPage,@Param("vo") SysLoginLogQueryVo sysLoginLogQueryVo);
}
