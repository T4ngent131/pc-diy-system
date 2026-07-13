package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.dto.OrderSubmitDTO;
import com.pcdiy.entity.Order;
import java.util.List;

public interface OrderService extends IService<Order> {
    List<Order> listWithConfig();
    Order getWithConfig(Long id);
    String createOrder(Order order, String configJson);
    String submitOrder(OrderSubmitDTO dto);
    List<Order> searchOrders(String keyword);
    boolean updateStatus(Long id, String status);
    long countPending();
}
