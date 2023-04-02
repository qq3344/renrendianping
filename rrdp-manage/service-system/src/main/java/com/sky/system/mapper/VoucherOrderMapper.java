package com.sky.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.model.dto.VoucherOrderQueryDTO;
import com.sky.model.evaluate.VoucherOrder;
import com.sky.model.vo.VoucherOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**

 * @version 1.0
 * @time 2023/3/9
 */
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    /**
     * 分页查询优惠券订单
     * @param iPage 分页对象
     * @param voucherOrderQueryDTO 表单查询对象
     * @return
     */
    IPage<VoucherOrderVo> queryVoucherOrderList(IPage<VoucherOrderVo> iPage,@Param("vo") VoucherOrderQueryDTO voucherOrderQueryDTO);
}
