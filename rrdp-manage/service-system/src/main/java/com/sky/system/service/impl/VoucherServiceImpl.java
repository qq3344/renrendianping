package com.sky.system.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.common.constant.RedisConstants;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.SeckillVoucher;
import com.sky.model.evaluate.Voucher;
import com.sky.model.vo.VoucherVo;
import com.sky.system.mapper.VoucherMapper;
import com.sky.system.service.SeckillVoucherService;
import com.sky.system.service.VoucherService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher>
        implements VoucherService {

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private SeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Transactional(rollbackFor = Exception.class)
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        // 保存秒杀卷数量到redis
        stringRedisTemplate.opsForValue().set(RedisConstants.SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
    }

    @Override
    public void saveVoucher(Voucher voucher) {
        // 新增秒杀券，添加缓存，库存
        if (voucher.getType() == 1) {
            addSeckillVoucher(voucher);
        } else {
            // 新增普通券
            this.save(voucher);
        }
    }

    @Override
    public void removeVoucherById(Long id) {
        // 无论是优惠劵还是秒杀券，直接删除redis中的缓存以及库存
        stringRedisTemplate.opsForValue().getAndDelete(RedisConstants.SECKILL_STOCK_KEY + id);
        // 删除库存
        seckillVoucherService.removeById(id);
        // 删除优惠劵
        this.removeById(id);
    }

    @Override
    public void updateVoucher(Voucher voucher) {
        if (voucher.getType() == 1) {
            // 先删除秒杀券，再添加秒杀劵
            // 1.删除秒杀券
            this.removeVoucherById(voucher.getId());
            // 2.添加秒杀券
            voucher.setId(null);
            this.saveVoucher(voucher);
        } else {
            // 如果是普通优惠券直接修改
            this.updateById(voucher);
        }

    }

    /**
     * 根据优惠券id返回vo视图
     *
     * @param id 优惠券id
     * @return
     */
    @Override
    public VoucherVo queryVoucherVoById(Integer id) {
        Voucher voucher = this.getById(id);
        if (voucher.getType() == 1) {
            return voucherMapper.queryVoucherVoById(id);
        }
        return voucherMapper.queryVoucherById(id);
    }

    /**
     * 分页查询优惠券对象
     *
     * @param page1        分页对象
     * @param nameQueryDTO 商铺名称查询
     * @return
     */
    @Override
    public IPage<VoucherVo> pageQuery(IPage<VoucherVo> page1, NameQueryDTO nameQueryDTO) {
        return voucherMapper.pageQuery(page1, nameQueryDTO);
    }
}
