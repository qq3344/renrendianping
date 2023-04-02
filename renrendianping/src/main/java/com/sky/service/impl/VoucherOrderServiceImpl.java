package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.dto.Result;
import com.sky.pojo.Voucher;
import com.sky.pojo.VoucherOrder;
import com.sky.mapper.VoucherOrderMapper;
import com.sky.service.ISeckillVoucherService;
import com.sky.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.IVoucherService;
import com.sky.utils.RedisIdWorker;
import com.sky.utils.UserHolder;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private IVoucherService voucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        // 指定lua脚本位置
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        // 指定脚本返回值
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    private IVoucherOrderService voucherOrderService;

    String queueName = "stream.orders";

    @PostConstruct
    public void init() {

        SECKILL_ORDER_EXECUTOR.submit(() -> {
            while (true) {
                try {
                    // 从消息队列中获取信息 userId orderId voucherId
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed()));
                    // 判断消息是否获取成功
                    // 获取失败，跳过这次
                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    // 获取成功！，可以下单
                    MapRecord<String, Object, Object> records = list.get(0);
                    Map<Object, Object> value = records.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                    // 创建订单
                    handleVoucherOrder(voucherOrder);
                    // ack确认
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", records.getId());
                } catch (Exception e) {
                    handlePendingList();
                }
            }
        });
    }

    private void handlePendingList() {
        while (true) {
            try {
                // 从消息队列中获取信息 userId orderId voucherId
                List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                        Consumer.from("g1", "c1"),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create(queueName, ReadOffset.from("0")));
                // 判断消息是否获取成功
                // 获取失败，结束
                if (list == null || list.isEmpty()) {
                    break;
                }
                // 获取成功！，可以下单
                MapRecord<String, Object, Object> records = list.get(0);
                Map<Object, Object> value = records.getValue();
                VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
                // 创建订单
                handleVoucherOrder(voucherOrder);
                // ack确认
                stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", records.getId());
            } catch (Exception e) {
                log.error("处理pending-list异常");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        // 创建Redisson锁对象,注意这里不能通过ThreadLocal获取用户id
        RLock lock = redissonClient.getLock("lock:order:" + voucherOrder.getUserId());
        //获取锁
        boolean isLock = false;
        isLock = lock.tryLock();
        // 判断锁是否获取成功
        if (!isLock) {
            // 获取失败
            return;
        }
        try {
            voucherOrderService.createVoucherOrder(voucherOrder);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用lua脚本实现秒杀下单
     *
     * @param voucherId 优惠卷id
     * @return
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        Voucher voucher = voucherService.getById(voucherId);
        // 判断是优惠券还是秒杀券
        // 优惠券
        if (voucher.getType() == 0) {
            // 订单id
            Long orderId = redisIdWorker.nextId("order");
            VoucherOrder voucherOrder = new VoucherOrder();
            voucherOrder.setId(orderId);
            voucherOrder.setUserId(UserHolder.getUser().getId());
            voucherOrder.setVoucherId(voucherId);
            this.save(voucherOrder);
            return Result.ok(orderId);
        } else {
            // 秒杀券
            // 订单id
            Long orderId = redisIdWorker.nextId("order");
            // 执行lua脚本
            Long result = stringRedisTemplate.execute(SECKILL_SCRIPT, Collections.emptyList(),
                    voucherId.toString(), UserHolder.getUser().getId().toString(), orderId.toString());
            int r = result.intValue();
            // 判断是否为0
            if (r != 0) {
                if (r == 1) {
                    return Result.fail("库存不足!");
                } else {
                    return Result.fail("请勿重复下单!");
                }
            }
            // 获取代理对象，处理事务
            voucherOrderService = (IVoucherOrderService) AopContext.currentProxy();
            // 返回订单id
            return Result.ok(orderId);
        }

    }

    @Override
    @NotNull
    @Transactional(rollbackFor = Exception.class)
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        // 不能通过ThreadLocal获取
        Long userId = voucherOrder.getUserId();
        // 查询订单，一人一单
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
        // 判断是否购买过
        if (count > 0) {
            //购买过
            return;
        }
        // 减少库存
        boolean flag = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherOrder.getVoucherId())
                //                .eq("stock",seckillVoucher.getStock())
                .gt("stock", 0)
                .update();
        if (!flag) {
            return;
        }
        // 保存订单信息到数据库
        this.save(voucherOrder);
    }



}
