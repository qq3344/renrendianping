package com.sky.model.evaluate;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_info")
@ApiModel("用户信息表")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty("主键userId")
    private Long userId;

    /**
     * 城市名称
     */
    @ApiModelProperty("城市名称")
    private String city;

    /**
     * 个人介绍，不要超过128个字符
     */
    @ApiModelProperty("个人介绍")
    private String introduce;

    /**
     * 粉丝数量
     */
    @ApiModelProperty("粉丝数量")
    private Integer fans;

    /**
     * 关注的人的数量
     */
    @ApiModelProperty("关注的人的数量")
    private Integer followee;

    /**
     * 性别，0：男，1：女
     */
    @ApiModelProperty("性别")
    private Boolean gender;

    /**
     * 生日
     */
    @ApiModelProperty("生日")
    private LocalDate birthday;

    /**
     * 积分
     */
    @ApiModelProperty("积分")
    private Integer credits;

    /**
     * 会员级别，0~9级,0代表未开通会员
     */
    @ApiModelProperty("会员级别")
    private Boolean level;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime updateTime;


}
