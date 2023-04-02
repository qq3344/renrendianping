package com.sky.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Voucher;
import com.sky.model.vo.VoucherVo;



public interface VoucherService extends IService<Voucher> {

    /**
     * 新增普通券和秒杀券
     * @param voucher 优惠券对象
     */
    void saveVoucher(Voucher voucher);

    /**
     * 删除优惠券
     * @param id 优惠券id
     */
    void removeVoucherById(Long id);

    /**
     * 更新优惠券
     * @param voucher 优惠券对象
     */
    void updateVoucher(Voucher voucher);

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
    IPage<VoucherVo> pageQuery(IPage<VoucherVo> page1, NameQueryDTO nameQueryDTO);
}
