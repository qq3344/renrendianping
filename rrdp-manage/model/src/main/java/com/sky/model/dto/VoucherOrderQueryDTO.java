package com.sky.model.dto;

import lombok.Data;

/**

 * @version 1.0
 * @time 2023/3/9
 */
@Data
public class VoucherOrderQueryDTO {
    private String shopName;
    private Long voucherType;
    private String nickName;
}
