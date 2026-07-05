package com.pcdiy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.entity.Configuration;
import com.pcdiy.entity.ConfigurationItem;
import com.pcdiy.mapper.ConfigurationItemMapper;
import com.pcdiy.mapper.ConfigurationMapper;
import com.pcdiy.service.ConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ConfigurationServiceImpl extends ServiceImpl<ConfigurationMapper, Configuration> implements ConfigurationService {

    private final ConfigurationItemMapper itemMapper;

    public ConfigurationServiceImpl(ConfigurationItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public Long createConfig(String name, String customer, String phone, String note, List<ConfigurationItem> items) {
        BigDecimal total = items.stream()
                .map(i -> i.getPrice() == null ? BigDecimal.ZERO : i.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Configuration config = new Configuration();
        config.setName(name);
        config.setCustomer(customer);
        config.setPhone(phone);
        config.setNote(note);
        config.setTotalPrice(total);
        config.setStatus("draft");
        save(config);

        for (ConfigurationItem item : items) {
            item.setConfigId(config.getId());
            itemMapper.insert(item);
        }
        return config.getId();
    }

    @Override
    public Configuration getWithItems(Long id) {
        return getById(id);
    }
}
