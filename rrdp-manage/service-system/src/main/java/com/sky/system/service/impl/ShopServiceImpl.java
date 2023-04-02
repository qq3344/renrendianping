package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Shop;
import com.sky.model.vo.ShopVo;
import com.sky.system.mapper.ShopMapper;
import com.sky.system.service.ShopService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/5
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
        implements ShopService {

    @Resource
    private ShopMapper shopMapper;

    @Override
    public IPage<ShopVo> pageQuery(IPage<ShopVo> iPage, NameQueryDTO nameQueryDTO) {
        return shopMapper.pageQuery(iPage,nameQueryDTO);
    }
}
