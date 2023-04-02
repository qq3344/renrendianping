package com.sky.service;

import com.sky.dto.Result;
import com.sky.pojo.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 秒杀下单
     * @param voucherId 优惠卷id
     * @return
     */
    Result seckillVoucher(Long voucherId);

    /**
     * 创建代理对象方法
     * @param voucherOrder
     */
    void createVoucherOrder(VoucherOrder voucherOrder);
}
