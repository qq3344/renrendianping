package com.sky.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.model.dto.VoucherOrderQueryDTO;
import com.sky.model.evaluate.VoucherOrder;
import com.sky.model.vo.VoucherOrderVo;
import com.sky.system.mapper.VoucherOrderMapper;
import com.sky.system.service.VoucherOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/9
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>
        implements VoucherOrderService {

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    /**
     * 分页查询优惠券订单
     * @param iPage 分页对象
     * @param voucherOrderQueryDTO 查询对象
     * @return
     */
    @Override
    public IPage<VoucherOrderVo> queryVoucherOrderList(IPage<VoucherOrderVo> iPage, VoucherOrderQueryDTO voucherOrderQueryDTO) {
        return voucherOrderMapper.queryVoucherOrderList(iPage,voucherOrderQueryDTO);
    }
}
