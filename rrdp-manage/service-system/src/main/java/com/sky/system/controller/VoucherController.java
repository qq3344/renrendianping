package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Voucher;
import com.sky.model.vo.VoucherVo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.VoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**

 * @version 1.0
 * @time 2023/3/5
 */
@Api(tags = "点评管理-优惠劵管理")
@RestController
@RequestMapping("/admin/evaluate/voucher")
public class VoucherController {

    @Resource
    private VoucherService voucherService;

    /**
     * {
     *     "shopId": 1,
     *     "title": "200元代金券",
     *     "subTitle": "周一到周日均可使用",
     *     "rules": "全场通用\\n无需预约\\n可无限叠加\\不兑现、不找零\\n仅限堂食",
     *     "payValue": 8000,
     *     "actualValue": 10000,
     *     "type": 1,
     *     "stock": 100,
     *     "beginTime":"2022-10-26T15:40:00",
     *     "endTime":"2022-11-26T23:40:00"
     * }
     * @param voucher
     * @return
     */
    @Log(title = "优惠劵管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.voucher.add')")
    @ApiOperation("添加优惠劵接口")
    @PostMapping("/saveVoucher")
    public Result<String> saveVoucher(@RequestBody Voucher voucher){
        voucherService.saveVoucher(voucher);
        return Result.ok();
    }

    @Log(title = "优惠劵管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.voucher.remove')")
    @ApiOperation("删除优惠劵接口")
    @DeleteMapping("/removeVoucherById/{id}")
    public Result<String> removeVoucherById(@PathVariable("id") Long id){
        voucherService.removeVoucherById(id);
        return Result.ok();
    }

    @Log(title = "优惠劵管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.voucher.update')")
    @ApiOperation("更新优惠劵接口")
    @PostMapping("/updateVoucher")
    public Result<String> updateVoucher(@RequestBody Voucher voucher){
        System.out.println(voucher);
        voucherService.updateVoucher(voucher);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.voucher.list')")
    @ApiOperation("根据id查询优惠劵信息详情")
    @GetMapping("/queryVoucherVoById/{id}")
    public Result<VoucherVo> queryVoucherVoById(@PathVariable("id") Integer id){
        VoucherVo voucherVo = voucherService.queryVoucherVoById(id);
        return Result.ok(voucherVo);
    }

    @GetMapping("/{page}/{limit}")
    @ApiOperation("优惠劵分页查询")
    @PreAuthorize("hasAuthority('bnt.voucher.list')")
    public Result<IPage<VoucherVo>> pageQuery(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit,
            NameQueryDTO nameQueryDTO){
        IPage<VoucherVo> page1 = new Page<>(page,limit);
        IPage<VoucherVo> voucherPage = voucherService.pageQuery(page1,nameQueryDTO);
        return Result.ok(voucherPage);
    }
}
