package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.dto.VoucherOrderQueryDTO;
import com.sky.model.evaluate.VoucherOrder;
import com.sky.model.vo.VoucherOrderVo;

/**

 * @version 1.0
 * @time 2023/3/9
 */
public interface VoucherOrderService extends IService<VoucherOrder> {
    /**
     * 分页查询优惠券订单
     * @param iPage 分页对象
     * @param voucherOrderQueryDTO 查询对象
     * @return
     */
    IPage<VoucherOrderVo> queryVoucherOrderList(IPage<VoucherOrderVo> iPage, VoucherOrderQueryDTO voucherOrderQueryDTO);
}
