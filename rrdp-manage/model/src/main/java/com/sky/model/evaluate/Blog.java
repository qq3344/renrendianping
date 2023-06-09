package com.sky.model.evaluate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog")
@ApiModel("博客表")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;
    /**
     * 商户id
     */
    @ApiModelProperty("商户id")
    private Long shopId;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;
    /**
     * 用户图标
     */
    @ApiModelProperty("用户图标")
    @TableField(exist = false)
    private String icon;
    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    @TableField(exist = false)
    private String name;
    /**
     * 是否点赞过了
     */
    @ApiModelProperty("是否点赞过了")
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    @ApiModelProperty("探店的照片")
    private String images;

    /**
     * 探店的文字描述
     */
    @ApiModelProperty("探店的文字描述")
    private String content;

    /**
     * 点赞数量
     */
    @ApiModelProperty("点赞数量")
    private Integer liked;

    /**
     * 评论数量
     */
    @ApiModelProperty("评论数量")
    private Integer comments;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;


}
