package com.sky.controller;


import com.sky.dto.Result;
import com.sky.pojo.ShopType;
import com.sky.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;

    /**
     * 返回商铺类型
     * @return
     */
    @GetMapping("list")
    public Result queryTypeList() {
        return typeService.queryList();
    }
}
