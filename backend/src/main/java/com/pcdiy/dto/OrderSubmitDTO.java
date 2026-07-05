package com.pcdiy.dto;

import lombok.Data;

@Data
public class OrderSubmitDTO {
    private String customer;
    private String phone;
    private String wechat;
    private String note;
    private java.math.BigDecimal totalPrice;

    // configItems: {"cpu":"cpu-001","gpu":"gpu-001",...}
    private String configItems;
}
