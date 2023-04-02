package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.evaluate.Shop;
import com.sky.model.vo.ShopVo;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "点评管理-商铺管理")
@RequestMapping("/admin/evaluate/shop")
@RestController
public class ShopController {

    @Resource
    private ShopService shopService;

    /**
     * 新增商铺信息
     * @param shop 商铺数据
     * @return 商铺id
     */
    @Log(title = "商铺管理",businessType = BusinessType.INSERT)
    @ApiOperation("商铺添加接口")
    //    @PreAuthorize("hasAuthority('bnt.shop.add')")
    @PostMapping("/save")
    public Result<String> saveShop(@RequestBody Shop shop) {
        shopService.save(shop);
        return Result.ok();
    }

    @Log(title = "商铺管理",businessType = BusinessType.DELETE)
    @ApiOperation("商铺删除接口")
    //    @PreAuthorize("hasAuthority('bnt.shop.remove')")
    @DeleteMapping("/remove/{id}")
    public Result<String> saveShop(@PathVariable("id") Integer id) {
        shopService.removeById(id);
        return Result.ok();
    }

    @Log(title = "商铺管理",businessType = BusinessType.UPDATE)
    @ApiOperation("商铺修改接口")
    //    @PreAuthorize("hasAuthority('bnt.shop.update')")
    @PostMapping("/update")
    public Result<String> updateShop(@RequestBody Shop shop) {
        shopService.updateById(shop);
        return Result.ok();
    }


    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页查询商铺类型接口")
//    @PreAuthorize("hasAuthority('bnt.shop.list')")
    public Result<IPage<ShopVo>> queryShopList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit,
            @ApiParam(name = "nameQueryDTO",value = "查询对象")
                    NameQueryDTO nameQueryDTO) {
        IPage<ShopVo> iPage = new Page<>(page,limit);
        IPage<ShopVo> shopPage = shopService.pageQuery(iPage,nameQueryDTO);
        return Result.ok(shopPage);
    }

    @ApiOperation("根据id查询店铺信息")
    @GetMapping("/{id}")
    public Result<Shop> queryById(@PathVariable("id") Integer id){
        Shop shop = shopService.getById(id);
        return Result.ok(shop);
    }


}
