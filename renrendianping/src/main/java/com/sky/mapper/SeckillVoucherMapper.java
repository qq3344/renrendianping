package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.pojo.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 Mapper 接口
 * </p>
 */
@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

}
