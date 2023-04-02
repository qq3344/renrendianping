package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.dto.VoucherOrderQueryDTO;
import com.sky.model.vo.VoucherOrderVo;
import com.sky.system.service.VoucherOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "点评管理-优惠券订单管理")
@RequestMapping("/admin/evaluate/voucherOrder")
@RestController
@Slf4j
public class VoucherOrderController {
    @Resource
    private VoucherOrderService voucherOrderService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页查询优惠券订单接口")
    @PreAuthorize("hasAuthority('bnt.voucherOrder.list')")
    public Result<IPage<VoucherOrderVo>> queryVoucherOrderList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit,
            @ApiParam(name = "voucherOrderQueryDTO",value = "查询对象")
                    VoucherOrderQueryDTO voucherOrderQueryDTO) {
        log.error(voucherOrderQueryDTO.getVoucherType()+"");
        IPage<VoucherOrderVo> iPage = new Page<>(page,limit);
        IPage<VoucherOrderVo> voucherOrderVoIPage = voucherOrderService.queryVoucherOrderList(iPage,voucherOrderQueryDTO);
        return Result.ok(voucherOrderVoIPage);
    }
}
