package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.system.SysRole;
import com.sky.model.vo.AssignRoleVo;
import com.sky.model.vo.SysRoleQueryVo;

import java.util.Map;

/**

 * @version 1.0
 * @time 2022/12/5
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 分页查询角色信息
     * @param iPage
     * @param sysRoleQueryVo
     * @return
     */
    IPage<SysRole> pageQuery(IPage<SysRole> iPage, SysRoleQueryVo sysRoleQueryVo);

    /**
     * 获取用户角色信息接口
     * @param userId
     * @return
     */
    Map<String, Object> toAssign(Long userId);

    /**
     * 分配用户角色信息接口
     * @param assignRoleVo
     */
    void doAssign(AssignRoleVo assignRoleVo);
}
