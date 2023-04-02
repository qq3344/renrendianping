package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Shop;
import com.sky.model.vo.ShopVo;

/**

 * @version 1.0
 * @time 2023/3/5
 */
public interface ShopService extends IService<Shop> {
    /**
     * 分页查询
     * @param iPage 分页对象
     * @param nameQueryDTO 查询对象
     * @return
     */
    IPage<ShopVo> pageQuery(IPage<ShopVo> iPage, NameQueryDTO nameQueryDTO);
}
