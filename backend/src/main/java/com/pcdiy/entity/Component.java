package com.pcdiy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("components")
public class Component {
    @TableId
    private String id;
    private String type;
    private String brand;
    private String model;
    private String specs;   // JSON string
    private java.math.BigDecimal price;
    private Integer stock;
    private String image;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
