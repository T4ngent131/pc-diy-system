package com.pcdiy.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConfigSubmitDTO {
    private String name;
    private String customer;
    private String phone;
    private String note;
    private List<ComponentItem> items;

    @Data
    public static class ComponentItem {
        private String type;
        private String componentId;
    }
}
