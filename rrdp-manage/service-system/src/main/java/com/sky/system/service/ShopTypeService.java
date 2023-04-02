package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.evaluate.ShopType;

/**

 * @version 1.0
 * @time 2023/3/4
 */
public interface ShopTypeService extends IService<ShopType> {
    /**
     * 分页查询商铺类型
     * @param iPage 分页对象
     * @return
     */
    IPage<ShopType> queryTypeList(IPage<ShopType> iPage);
}
