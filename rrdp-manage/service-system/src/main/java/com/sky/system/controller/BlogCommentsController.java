package com.sky.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.common.result.Result;
import com.sky.model.dto.CommentQueryDTO;
import com.sky.model.vo.BlogCommentsVo;
import com.sky.system.service.BlogCommentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "点评管理-博客评论管理")
@RequestMapping("/admin/evaluate/blogComments")
@RestController
public class BlogCommentsController {

    @Resource
    private BlogCommentsService blogCommentsService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("分页博客评论接口")
    @PreAuthorize("hasAuthority('bnt.blogComments.list')")
    public Result<IPage<BlogCommentsVo>> queryBlogCommentsList(
            @ApiParam(name = "page",value = "当前页码",required = true)
            @PathVariable("page")
                    Integer page,
            @ApiParam(name = "limit",value = "每页记录数",required = true)
            @PathVariable("limit")
                    Integer limit,
            @ApiParam(name = "commentQueryDTO",value = "查询对象")
                    CommentQueryDTO commentQueryDTO) {
        IPage<BlogCommentsVo> iPage = new Page<>(page,limit);
        IPage<BlogCommentsVo> blogCommentsList = blogCommentsService.queryBlogCommentsList(iPage,commentQueryDTO);
        return Result.ok(blogCommentsList);
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation("删除博客评论接口")
    @PreAuthorize("hasAuthority('bnt.blog.list')")
    public Result<String> removeBlogById(@PathVariable("id") Integer id){
        return Result.ok();
    }
}
