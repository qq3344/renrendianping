package com.sky.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 * @version 1.0
 * @time 2023/3/6
 */
@Data
public class NameQueryDTO implements Serializable {

    public String keyword;

}
