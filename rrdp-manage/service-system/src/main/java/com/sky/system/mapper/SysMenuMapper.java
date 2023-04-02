package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**

 * @version 1.0
 * @time 2022/12/10
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据用户id获取所有的菜单
     * @param id
     * @return
     */
    List<SysMenu> getListByUserId(Long id);
}
