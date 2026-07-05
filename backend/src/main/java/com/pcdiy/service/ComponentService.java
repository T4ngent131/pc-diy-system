package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.entity.Component;
import java.util.List;
import java.util.Map;

public interface ComponentService extends IService<Component> {
    List<Component> getByType(String type);
    Map<String, List<Component>> getAllGrouped();
    List<String> getBrands(String type);
    List<Component> getLowStock();
}
