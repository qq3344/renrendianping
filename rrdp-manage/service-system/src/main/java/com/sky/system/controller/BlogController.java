package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.dto.NameQueryDTO;
import com.sky.model.vo.BlogVo;
import com.sky.system.service.BlogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "点评管理-日记管理")
@RestController
@RequestMapping("/admin/evaluate/blog")
public class BlogController {

    @Resource
    private BlogService blogService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页博客日记接口")
    @PreAuthorize("hasAuthority('bnt.blog.list')")
    public Result<IPage<BlogVo>> queryBlogList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit,
            @ApiParam(name = "nameQueryDTO",value = "查询对象")
                    NameQueryDTO nameQueryDTO) {
        IPage<BlogVo> iPage = new Page<>(page,limit);
        IPage<BlogVo> blogPage = blogService.queryBlogList(iPage,nameQueryDTO);
        return Result.ok(blogPage);
    }

    @DeleteMapping("/removeBlogById/{id}")
    @ApiOperation("删除博客日记接口")
    @PreAuthorize("hasAuthority('bnt.blog.list')")
    public Result<String> removeBlogById(@PathVariable("id") Integer id){
        blogService.removeBlogById(id);
        return Result.ok();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询博客")
    @PreAuthorize("hasAuthority('bnt.blog.search')")
    public Result<BlogVo> queryBlogVoById(@PathVariable("id") Integer id){
        BlogVo blogVo =  blogService.queryBlogVoById(id);
        return Result.ok(blogVo);
    }



}
