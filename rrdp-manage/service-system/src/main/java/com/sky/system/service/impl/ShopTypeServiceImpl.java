package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.evaluate.ShopType;
import com.sky.system.mapper.ShopTypeMapper;
import com.sky.system.service.ShopTypeService;
import org.springframework.stereotype.Service;

/**

 * @version 1.0
 * @time 2023/3/4
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType>
        implements ShopTypeService {
    /**
     * 分页查询商铺类型
     * @param iPage 分页对象
     * @return
     */
    @Override
    public IPage<ShopType> queryTypeList(IPage<ShopType> iPage) {
        LambdaQueryWrapper<ShopType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ShopType::getSort);
        IPage<ShopType> page = this.page(iPage, queryWrapper);
        return page;
    }
}
