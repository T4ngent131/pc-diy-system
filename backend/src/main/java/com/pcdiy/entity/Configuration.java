package com.pcdiy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("configurations")
public class Configuration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String customer;
    private String phone;
    private String note;
    private java.math.BigDecimal totalPrice;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
