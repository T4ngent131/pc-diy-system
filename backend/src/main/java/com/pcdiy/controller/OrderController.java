package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.dto.OrderSubmitDTO;
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

    @GetMapping
    public ApiResponse<List<Order>> list() {
        return ApiResponse.success(orderService.listWithConfig());
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getWithConfig(id));
    }

    @PostMapping
    public ApiResponse<String> create(@RequestBody OrderSubmitDTO dto) {
        try {
            String orderNo = orderService.submitOrder(dto);
            return ApiResponse.success("Order created", orderNo);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean ok = orderService.updateStatus(id, status);
        return ok ? ApiResponse.success(null) : ApiResponse.error("Order not found");
    }

    @GetMapping("/search")
    public ApiResponse<List<Order>> search(@RequestParam String keyword) {
        return ApiResponse.success(orderService.searchOrders(keyword));
    }

    @GetMapping("/pending-count")
    public ApiResponse<Long> pendingCount() {
        return ApiResponse.success(orderService.countPending());
    }
}
