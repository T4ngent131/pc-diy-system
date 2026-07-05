package com.pcdiy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("configuration_items")
public class ConfigurationItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long configId;
    private String componentType;
    private String componentId;
    private String componentName;
    private BigDecimal price;
}
