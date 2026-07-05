package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.entity.Order;
import java.util.List;

public interface OrderService extends IService<Order> {
    String createOrder(Order order, String configJson);
    List<Order> searchOrders(String keyword);
    boolean updateStatus(Long id, String status);
    long countPending();
}
