package com.pcdiy.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderSubmitDTO {
    private String customer;
    private String phone;
    private String wechat;
    private String note;
    private java.math.BigDecimal totalPrice;

    private List<ConfigSubmitDTO.ComponentItem> items;
}
