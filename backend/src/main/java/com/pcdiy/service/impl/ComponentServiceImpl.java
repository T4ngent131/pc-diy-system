package com.pcdiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.entity.Component;
import com.pcdiy.mapper.ComponentMapper;
import com.pcdiy.service.ComponentService;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponentServiceImpl extends ServiceImpl<ComponentMapper, Component> implements ComponentService {

    @Override
    public List<Component> getByType(String type) {
        return baseMapper.selectByType(type);
    }

    @Override
    public Map<String, List<Component>> getAllGrouped() {
        List<Component> all = list();
        return all.stream().collect(Collectors.groupingBy(Component::getType, LinkedHashMap::new, Collectors.toList()));
    }

    @Override
    public List<String> getBrands(String type) {
        return baseMapper.selectBrandsByType(type);
    }

    @Override
    public List<Component> getLowStock() {
        return baseMapper.selectLowStock();
    }
}
