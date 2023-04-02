package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.system.SysMenu;
import com.sky.model.system.SysRoleMenu;
import com.sky.model.vo.AssignMenuVo;
import com.sky.model.vo.RouterVo;
import com.sky.system.handler.MyException;
import com.sky.system.mapper.SysMenuMapper;
import com.sky.system.service.SysMenuService;
import com.sky.system.service.SysRoleMenuService;
import com.sky.system.util.RouterHelper;
import com.sky.system.util.TreeMenuHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Transactional(rollbackFor = Exception.class)
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 删除菜单
     * @param id
     */
    @Override
    public void removeSysMenu(Long id) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId,id);
        long count = this.count(queryWrapper);
        if (count > 0){
            throw new MyException(201,"当前菜单下有子菜单，请先删除子菜单!");
        }
        this.removeById(id);
    }

    /**
     * 菜单数据构成树形数据
     * @return
     */
    @Override
    public List<SysMenu> treeMenu() {
        List<SysMenu> sysMenus = this.list();
        return TreeMenuHelper.buildTreeMenu(sysMenus);
    }

    /**
     * 根据角色获取菜单
     * @param roleId
     * @return
     */
    @Override
    public List<SysMenu> toAssign(Long roleId) {
        // 获取所有的菜单信息
        LambdaQueryWrapper<SysMenu> menuQueryWrapper = new LambdaQueryWrapper<>();
        menuQueryWrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> menuList = this.list(menuQueryWrapper);
        // 获取用户的菜单权限
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(queryWrapper);
        // 设置菜单是否被选中
        List<Long> menus = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        menuList.forEach(sysMenu -> sysMenu.setSelect(menus.contains(sysMenu.getId())));
        // 转为树形结构
        return TreeMenuHelper.buildTreeMenu(menuList);
    }

    /**
     * 给角色分配菜单
     * @param assignMenuVo
     */
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        // 先删除所有的菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(queryWrapper);
        // 添加
        for (Long menuId : assignMenuVo.getMenuIdList()) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    /**
     * 根据角色id获取菜单,返回所有的选中的MenuId
     * @param roleId
     * @return
     */
    @Override
    public List<Long> getSelectMenuId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(queryWrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    /**
     * 获取用户的按钮权限
     * @param id
     * @return
     */
    @Override
    public List<String> queryButtonPermsList(Long id) {
        // 如果是admin，所有的按钮权限都有,先返回菜单
        List<SysMenu> sysMenuList = null;
        if (id == 1){
           sysMenuList = this.query().eq("status", 1).list();
        }else{
            sysMenuList = baseMapper.getListByUserId(id);
        }
        // 遍历菜单，将权限返回
        return sysMenuList.stream()
                .filter(sysMenu -> sysMenu.getType() == 2)
                .map(SysMenu::getPerms).collect(Collectors.toList());
    }

    /**
     * 获取用户菜单
     * @param id
     * @return
     */
    @Override
    public List<RouterVo> getUserMenuList(Long id) {
        // 如果是admin，所有的权限都有,先返回菜单
        List<SysMenu> sysMenuList = null;
        if (id == 1){
            sysMenuList = this.query()
                    .orderByAsc("sort_value")
                    .eq("status", 1).list();
        }else{
            sysMenuList = baseMapper.getListByUserId(id);
        }
        // 遍历菜单
        List<SysMenu> menuList = TreeMenuHelper.buildTreeMenu(sysMenuList);

        List<RouterVo> routerVoList = RouterHelper.buildRouters(menuList);

        return routerVoList;
    }
}
