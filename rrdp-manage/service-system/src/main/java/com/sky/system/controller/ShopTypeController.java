package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.evaluate.ShopType;
import com.sky.system.annotation.Log;
import com.sky.system.enums.BusinessType;
import com.sky.system.service.ShopTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 * @version 1.0
 * @time 2023/3/4
 */
@Api(tags = "点评管理-商铺类型管理")
@RestController
@RequestMapping("/admin/evaluate/shopType")
public class ShopTypeController {

    @Resource
    private ShopTypeService shopTypeService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页查询商铺类型接口")
    @PreAuthorize("hasAuthority('bnt.shopType.list')")
    public Result<IPage<ShopType>> queryTypeList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit) {
        IPage<ShopType> iPage = new Page<>(page,limit);
        IPage<ShopType> typeIPage = shopTypeService.queryTypeList(iPage);
        return Result.ok(typeIPage);
    }

    @Log(title = "商铺类型管理",businessType = BusinessType.DELETE)
//    @PreAuthorize("hasAuthority('bnt.shopeType.remove')")
    @ApiOperation("删除单个商铺类型接口")
    @DeleteMapping("/remove/{id}")
    public Result<String> remove(@PathVariable("id") Long id){
        shopTypeService.removeById(id);
        return Result.ok();
    }


    @Log(title = "商铺类型管理",businessType = BusinessType.INSERT)
//    @PreAuthorize("hasAuthority('bnt.shopType.add')")
    @ApiOperation("添加商铺类型接口")
    @PostMapping("/save")
    public Result<String> save(@RequestBody ShopType shopType){
        System.out.println(shopType);
        shopTypeService.save(shopType);
        return Result.ok();
    }

    @Log(title = "商铺类型管理",businessType = BusinessType.UPDATE)
//    @PreAuthorize("hasAuthority('bnt.shopeType.update')")
    @ApiOperation("修改商铺类型接口")
    @PostMapping("/update")
    public Result<String> update(@RequestBody ShopType shopType){
        shopTypeService.updateById(shopType);
        return Result.ok();
    }

    @ApiOperation("根据id查询单个类型信息")
    @GetMapping("/{id}")
    public Result<ShopType> queryById(@PathVariable("id") Integer id){
        ShopType shopType = shopTypeService.getById(id);
        return Result.ok(shopType);
    }

    @ApiOperation("查询所有的商铺类型呢")
    @GetMapping("/queryAll")
    public Result<List<ShopType>> queryAll(){
        List<ShopType> shopTypeList = shopTypeService.list();
        return  Result.ok(shopTypeList);
    }

}
