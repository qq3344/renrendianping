package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.model.system.SysOperLog;
import com.sky.model.vo.SysOperLogQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/2/6
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
    /**
     * 分页查询操作日志
     * @param sysOperLogPage  分页对象
     * @param sysOperLogQueryVo 查询对象
     * @return
     */
    Page<SysOperLog> pageQuery(IPage<SysOperLog> sysOperLogPage,@Param("vo") SysOperLogQueryVo sysOperLogQueryVo);
}
