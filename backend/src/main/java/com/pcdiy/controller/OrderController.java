package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.entity.Order;
import com.pcdiy.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 获取所有订单 */
    @GetMapping
    public ApiResponse<List<Order>> list() {
        return ApiResponse.success(orderService.list());
    }

    /** 获取订单详情 */
    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getById(id));
    }

    /** 创建订单（用户端） */
    @PostMapping
    public ApiResponse<String> create(@RequestBody Order order) {
        String orderNo = orderService.createOrder(order, null);
        return ApiResponse.success("订单创建成功", orderNo);
    }

    /** 更新订单状态（管理端） */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean ok = orderService.updateStatus(id, status);
        return ok ? ApiResponse.success(null) : ApiResponse.error("订单不存在");
    }

    /** 搜索订单 */
    @GetMapping("/search")
    public ApiResponse<List<Order>> search(@RequestParam String keyword) {
        return ApiResponse.success(orderService.searchOrders(keyword));
    }

    /** 待处理订单数量 */
    @GetMapping("/pending-count")
    public ApiResponse<Long> pendingCount() {
        return ApiResponse.success(orderService.countPending());
    }
}
