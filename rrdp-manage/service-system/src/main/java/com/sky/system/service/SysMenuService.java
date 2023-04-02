package com.sky.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.system.SysMenu;
import com.sky.model.vo.AssignMenuVo;
import com.sky.model.vo.RouterVo;

import java.util.List;


public interface SysMenuService extends IService<SysMenu> {
    /**
     * 删除菜单
     * @param id
     */
    void removeSysMenu(Long id);

    /**
     * 菜单数据构成树形数据
     * @return
     */
    List<SysMenu> treeMenu();

    /**
     * 根据角色获取菜单
     * @param roleId
     * @return
     */
    List<SysMenu> toAssign(Long roleId);

    /**
     * 给角色分配菜单
     * @param assignMenuVo
     */
    void doAssign(AssignMenuVo assignMenuVo);

    /**
     * 根据角色id获取菜单,返回所有的选中的MenuId
     * @param roleId
     * @return
     */
    List<Long> getSelectMenuId(Long roleId);

    /**
     * 获取用户的按钮权限
     * @param id
     * @return
     */
    List<String> queryButtonPermsList(Long id);

    /**
     * 获取用户菜单
     * @param id
     * @return
     */
    List<RouterVo> getUserMenuList(Long id);
}
