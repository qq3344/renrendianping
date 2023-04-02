package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.system.SysRole;
import com.sky.model.system.SysUserRole;
import com.sky.model.vo.AssignRoleVo;
import com.sky.model.vo.SysRoleQueryVo;
import com.sky.system.mapper.SysRoleMapper;
import com.sky.system.service.SysRoleService;
import com.sky.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**

 * @version 1.0
 * @time 2022/12/5
 */
@Transactional
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleService sysUserRoleService;

    /**
     * 分页查询角色信息
     * @param iPage
     * @param sysRoleQueryVo
     * @return
     */
    @Override
    public IPage<SysRole> pageQuery(IPage<SysRole> iPage, SysRoleQueryVo sysRoleQueryVo) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(sysRoleQueryVo.getRoleName()),SysRole::getRoleName,sysRoleQueryVo.getRoleName());
        return this.page(iPage, queryWrapper);
    }

    /**
     * 获取用户角色信息接口
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> toAssign(Long userId) {
        Map<String, Object> roleMap = new HashMap<>();
        // 获取所有的角色
        List<SysRole> sysRoleList = this.list();
        // 获取用户的角色信息id
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> userRoles = sysUserRoleService.list(queryWrapper);
        List<Long> userRoleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        roleMap.put("allRoles",sysRoleList);
        roleMap.put("userRoleIds",userRoleIds);
        return roleMap;
    }

    /**
     * 分配用户角色信息接口
     * @param assignRoleVo
     */
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        // 先删除已经绑定了的角色信息
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId,assignRoleVo.getUserId());
        sysUserRoleService.remove(deleteWrapper);
        // 给用户添加角色
        assignRoleVo.getRoleIdList()
                .forEach(roleId -> {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(assignRoleVo.getUserId());
                    sysUserRole.setRoleId(roleId);
                    sysUserRoleService.save(sysUserRole);
                });
    }
}
