package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.entity.Configuration;
import com.pcdiy.entity.ConfigurationItem;
import java.util.List;

public interface ConfigurationService extends IService<Configuration> {
    Long createConfig(String name, String customer, String phone, String note, List<ConfigurationItem> items);
    Configuration getWithItems(Long id);
}
