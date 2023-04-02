package com.sky.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户查询表单

 * @version 1.0
 * @time 2023/3/1
 */
@Data
@ApiModel("分页查询用户表单")
public class UserQueryVo {

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("创建时间开始")
    private String createTimeBegin;

    @ApiModelProperty("创建时间结束")
    private String createTimeEnd;
}
