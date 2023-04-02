package com.sky.system.util;

import com.sky.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**

 * @version 1.0
 * @time 2022/12/10
 */
public class TreeMenuHelper {

    public static List<SysMenu> buildTreeMenu(List<SysMenu> sysMenus){
        List<SysMenu> tree = new ArrayList<>();
        sysMenus.forEach(sysMenu -> {
            // 找到顶级菜单
            if (sysMenu.getParentId() == 0) {
                // 递归查找子级
                tree.add(addSubSysMenu(sysMenu,sysMenus));
            }
        });
        return tree;
    }

    public static SysMenu addSubSysMenu(SysMenu sysMenu,List<SysMenu> sysMenus){
        sysMenu.setChildren(new ArrayList<>());
        sysMenus.forEach(menu -> {
            if (sysMenu.getId().equals(menu.getParentId())){
                if (sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(addSubSysMenu(menu,sysMenus));
            }
        });
        return sysMenu;
    }
}
