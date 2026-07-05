package com.pcdiy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory_records")
public class InventoryRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String componentId;
    private String componentName;
    private String type;     // "in" or "out"
    private Integer quantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String remark;
    private String operator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
