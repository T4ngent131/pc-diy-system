package com.pcdiy.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.entity.Order;
import com.pcdiy.mapper.OrderMapper;
import com.pcdiy.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    @Transactional
    public String createOrder(Order order, String configJson) {
        // Generate order_no: ORD-yyyyMMdd-xxx
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
    public List<Order> searchOrders(String keyword) {
        return baseMapper.search(keyword);
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
}
