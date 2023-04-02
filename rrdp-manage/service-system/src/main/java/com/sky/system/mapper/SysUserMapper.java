package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.system.SysUser;
import com.sky.model.vo.SysUserQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2022/12/9
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    IPage<SysUser> pageQuery(IPage<SysUser> userPage,@Param("vo") SysUserQueryVo sysUserQueryVo);
}
