package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Voucher;
import com.sky.model.vo.VoucherVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/5
 */
@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
    /**
     * 根据优惠券id返回vo视图
     * @param id 优惠券id
     * @return
     */
    VoucherVo queryVoucherVoById(Integer id);

    /**
     * 分页查询优惠券
     * @param page1 分页对象
     * @param nameQueryDTO 商铺名称查询
     * @return
     */
    IPage<VoucherVo> pageQuery(IPage<VoucherVo> page1,@Param("vo") NameQueryDTO nameQueryDTO);

    /**
     * 根据id查询普通优惠券
     * @param id
     * @return
     */
    VoucherVo queryVoucherById(Integer id);
}
