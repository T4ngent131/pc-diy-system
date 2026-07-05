package com.pcdiy.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private String componentId;
    private int quantity;
    private String remark;
}
