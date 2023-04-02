package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IShopTypeService extends IService<ShopType> {

    /**
     * 返回商铺类型
     * @return
     */
    Result queryList();
}
