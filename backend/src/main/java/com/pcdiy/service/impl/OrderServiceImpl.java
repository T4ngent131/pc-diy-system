package com.pcdiy.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.dto.ConfigSubmitDTO;
import com.pcdiy.dto.OrderSubmitDTO;
import com.pcdiy.entity.Component;
import com.pcdiy.entity.ConfigurationItem;
import com.pcdiy.entity.Order;
import com.pcdiy.mapper.ConfigurationItemMapper;
import com.pcdiy.mapper.OrderMapper;
import com.pcdiy.service.ComponentService;
import com.pcdiy.service.ConfigurationService;
import com.pcdiy.service.InventoryRecordService;
import com.pcdiy.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ComponentService componentService;
    private final ConfigurationService configurationService;
    private final InventoryRecordService inventoryRecordService;
    private final ConfigurationItemMapper configurationItemMapper;

    public OrderServiceImpl(ComponentService componentService,
                            ConfigurationService configurationService,
                            InventoryRecordService inventoryRecordService,
                            ConfigurationItemMapper configurationItemMapper) {
        this.componentService = componentService;
        this.configurationService = configurationService;
        this.inventoryRecordService = inventoryRecordService;
        this.configurationItemMapper = configurationItemMapper;
    }

    @Override
    public List<Order> listWithConfig() {
        List<Order> orders = list();
        for (Order order : orders) {
            attachConfigSummary(order);
        }
        return orders;
    }

    @Override
    public Order getWithConfig(Long id) {
        Order order = getById(id);
        if (order != null) {
            attachConfigSummary(order);
        }
        return order;
    }

    @Override
    @Transactional
    public String createOrder(Order order, String configJson) {
        String datePart = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        long count = count(new LambdaQueryWrapper<Order>()
                .likeRight(Order::getOrderNo, "ORD-" + datePart));
        String orderNo = String.format("ORD-%s-%03d", datePart, count + 1);
        order.setOrderNo(orderNo);
        order.setStatus("pending");
        save(order);
        return orderNo;
    }

    @Override
    @Transactional
    public String submitOrder(OrderSubmitDTO dto) {
        validateSubmit(dto);

        List<ConfigSubmitDTO.ComponentItem> selectedItems = dto.getItems() == null
                ? List.of()
                : dto.getItems().stream()
                    .filter(item -> item.getComponentId() != null && !item.getComponentId().isBlank())
                    .toList();
        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("At least one component is required");
        }

        List<ConfigurationItem> configItems = selectedItems.stream()
                .map(this::buildConfigItem)
                .toList();

        BigDecimal totalPrice = dto.getTotalPrice() != null
                ? dto.getTotalPrice()
                : configItems.stream()
                    .map(ConfigurationItem::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long configId = null;
        if (!configItems.isEmpty()) {
            configId = configurationService.createConfig("Submitted build", dto.getCustomer(), dto.getPhone(), dto.getNote(), configItems);
        }

        Order order = new Order();
        order.setCustomer(dto.getCustomer());
        order.setPhone(dto.getPhone());
        order.setWechat(dto.getWechat());
        order.setNote(dto.getNote());
        order.setTotalPrice(totalPrice);
        order.setConfigId(configId);

        String orderNo = createOrder(order, null);
        deductStock(configItems);
        return orderNo;
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        List<Order> orders = baseMapper.search(keyword);
        for (Order order : orders) {
            attachConfigSummary(order);
        }
        return orders;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Order order = getById(id);
        if (order == null) return false;
        order.setStatus(status);
        return updateById(order);
    }

    @Override
    public long countPending() {
        return baseMapper.countPending();
    }

    private void validateSubmit(OrderSubmitDTO dto) {
        if (dto.getCustomer() == null || dto.getCustomer().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (dto.getPhone() == null || dto.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }

    private ConfigurationItem buildConfigItem(ConfigSubmitDTO.ComponentItem item) {
        Component component = componentService.getById(item.getComponentId());
        if (component == null) {
            throw new IllegalArgumentException("Component not found: " + item.getComponentId());
        }
        if (component.getStock() == null || component.getStock() < 1) {
            throw new IllegalArgumentException("Insufficient stock: " + component.getBrand() + " " + component.getModel());
        }

        ConfigurationItem configItem = new ConfigurationItem();
        configItem.setComponentType(item.getType());
        configItem.setComponentId(component.getId());
        configItem.setComponentName(component.getBrand() + " " + component.getModel());
        configItem.setPrice(component.getPrice());
        return configItem;
    }

    private void deductStock(List<ConfigurationItem> configItems) {
        for (ConfigurationItem item : configItems) {
            Component component = componentService.getById(item.getComponentId());
            int before = component.getStock();
            component.setStock(before - 1);
            componentService.updateById(component);
            inventoryRecordService.recordOut(component.getId(), item.getComponentName(), 1, before, component.getStock(), "order");
        }
    }

    private void attachConfigSummary(Order order) {
        if (order.getConfigId() == null) {
            order.setConfig(Map.of());
            return;
        }

        List<ConfigurationItem> items = configurationItemMapper.selectList(
                new LambdaQueryWrapper<ConfigurationItem>()
                        .eq(ConfigurationItem::getConfigId, order.getConfigId())
        );
        Map<String, String> config = items.stream()
                .collect(Collectors.toMap(
                        ConfigurationItem::getComponentType,
                        ConfigurationItem::getComponentName,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        order.setConfig(config);
    }
}
