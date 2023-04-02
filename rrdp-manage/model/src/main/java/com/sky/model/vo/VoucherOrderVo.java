package com.sky.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**

 * @version 1.0
 * @time 2023/3/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("优惠券订单返回视图类")
public class VoucherOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("主键id")
    private Long id;

    /**
     * 优惠券标题
     */
    @ApiModelProperty("优惠券标题")
    private String title;

    /**
     * 优惠券类型id
     */
    @ApiModelProperty("优惠券类型id")
    private Long voucherTypeId;

    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    @ApiModelProperty("支付方式")
    private Integer payType;

    /**
     * 订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
     */
    @ApiModelProperty("订单状态")
    private Integer status;

    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 消费者
     */
    @ApiModelProperty("消费者")
    private String nickName;

    /**
     * 商铺名称
     */
    @ApiModelProperty("商铺名称")
    private String shopName;

    /**
     * 商铺类型
     */
    @ApiModelProperty("商铺类型")
    private String shopType;

}
