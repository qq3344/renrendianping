package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Shop;
import com.sky.model.vo.ShopVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/5
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
    /**
     * 分页查询
     * @param iPage 分页对象
     * @param nameQueryDTO 查询对象
     * @return
     */
    IPage<ShopVo> pageQuery(IPage<ShopVo> iPage,@Param("vo") NameQueryDTO nameQueryDTO);
}
