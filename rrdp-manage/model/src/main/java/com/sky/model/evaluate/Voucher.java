package com.sky.model.evaluate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2022-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher")
@ApiModel("优惠券表")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 商铺id
     */
    @ApiModelProperty("商铺id")
    private Long shopId;

    /**
     * 代金券标题
     */
    @ApiModelProperty("代金券标题")
    private String title;

    /**
     * 副标题
     */
    @ApiModelProperty("副标题")
    private String subTitle;

    /**
     * 使用规则
     */
    @ApiModelProperty("使用规则")
    private String rules;

    /**
     * 支付金额
     */
    @ApiModelProperty("支付金额")
    private Long payValue;

    /**
     * 抵扣金额
     */
    @ApiModelProperty("抵扣金额")
    private Long actualValue;

    /**
     * 优惠券类型
     */
    @ApiModelProperty("优惠券类型")
    private Integer type;

    /**
     * 优惠券状态
     */
    @ApiModelProperty("优惠券状态")
    private Integer status;
    /**
     * 库存
     */
    @ApiModelProperty("库存")
    @TableField(exist = false)
    private Integer stock;

    /**
     * 生效时间
     */
    @ApiModelProperty("生效时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    @ApiModelProperty("失效时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
