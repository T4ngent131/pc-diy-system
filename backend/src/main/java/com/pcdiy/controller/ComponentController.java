package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.entity.Component;
import com.pcdiy.service.ComponentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/components")
public class ComponentController {

    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    /** 获取某类配件 */
    @GetMapping("/type/{type}")
    public ApiResponse<List<Component>> getByType(@PathVariable String type) {
        return ApiResponse.success(componentService.getByType(type));
    }

    /** 获取所有配件（按类型分组） */
    @GetMapping("/all")
    public ApiResponse<Map<String, List<Component>>> getAll() {
        return ApiResponse.success(componentService.getAllGrouped());
    }

    /** 获取某类配件的品牌列表 */
    @GetMapping("/brands/{type}")
    public ApiResponse<List<String>> getBrands(@PathVariable String type) {
        return ApiResponse.success(componentService.getBrands(type));
    }

    /** 获取低库存配件 */
    @GetMapping("/low-stock")
    public ApiResponse<List<Component>> getLowStock() {
        return ApiResponse.success(componentService.getLowStock());
    }

    /** 更新配件库存 */
    @PutMapping("/{id}/stock")
    public ApiResponse<Void> updateStock(@PathVariable String id, @RequestParam int stock) {
        Component c = componentService.getById(id);
        if (c != null) {
            c.setStock(stock);
            componentService.updateById(c);
        }
        return ApiResponse.success(null);
    }

    /** 配件详情 */
    @GetMapping("/{id}")
    public ApiResponse<Component> getById(@PathVariable String id) {
        return ApiResponse.success(componentService.getById(id));
    }

    /** 搜索配件 */
    @GetMapping("/search")
    public ApiResponse<List<Component>> search(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String brand) {
        List<Component> all = type != null ? componentService.getByType(type) : componentService.list();
        if (q != null && !q.isEmpty()) {
            all = all.stream().filter(c -> c.getModel().toLowerCase().contains(q.toLowerCase())).toList();
        }
        if (brand != null && !brand.isEmpty()) {
            all = all.stream().filter(c -> c.getBrand().equals(brand)).toList();
        }
        return ApiResponse.success(all);
    }
}
