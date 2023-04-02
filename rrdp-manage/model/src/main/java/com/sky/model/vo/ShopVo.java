package com.sky.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**

 * @version 1.0
 * @time 2023/3/6
 */
@Data
@ApiModel("商铺返回类")
public class ShopVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 商铺名称
     */
    @ApiModelProperty("商铺名称")
    private String name;

    /**
     * 商铺类型的id
     */
    @ApiModelProperty("商铺类型的id")
    private Long typeId;

    /**
     * 商铺类型的名称
     */
    @ApiModelProperty("商铺类型的名称")
    private String typeName;

    /**
     * 商铺图片，多个图片以','隔开
     */
    @ApiModelProperty("商铺图片")
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    @ApiModelProperty("商圈")
    private String area;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    private String address;

    /**
     * 均价，取整数
     */
    @ApiModelProperty("均价")
    private Long avgPrice;

    /**
     * 销量
     */
    @ApiModelProperty("销量")
    private Integer sold;

    /**
     * 评论数量
     */
    @ApiModelProperty("评论数量")
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    @ApiModelProperty("评分")
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    @ApiModelProperty("营业时间")
    private String openHours;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
