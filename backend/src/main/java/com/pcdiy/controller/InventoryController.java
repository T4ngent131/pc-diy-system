package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.dto.InventoryDTO;
import com.pcdiy.entity.Component;
import com.pcdiy.entity.InventoryRecord;
import com.pcdiy.service.ComponentService;
import com.pcdiy.service.InventoryRecordService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ComponentService componentService;
    private final InventoryRecordService recordService;

    public InventoryController(ComponentService componentService, InventoryRecordService recordService) {
        this.componentService = componentService;
        this.recordService = recordService;
    }

    /** 入库 */
    @PostMapping("/in")
    public ApiResponse<Void> stockIn(@RequestBody InventoryDTO dto) {
        Component c = componentService.getById(dto.getComponentId());
        if (c == null) return ApiResponse.error("配件不存在");
        int before = c.getStock();
        c.setStock(before + dto.getQuantity());
        componentService.updateById(c);
        recordService.recordIn(dto.getComponentId(), c.getBrand() + " " + c.getModel(),
                dto.getQuantity(), before, c.getStock(), dto.getRemark());
        return ApiResponse.success(null);
    }

    /** 出库 */
    @PostMapping("/out")
    public ApiResponse<Void> stockOut(@RequestBody InventoryDTO dto) {
        Component c = componentService.getById(dto.getComponentId());
        if (c == null) return ApiResponse.error("配件不存在");
        int before = c.getStock();
        if (before < dto.getQuantity()) return ApiResponse.error("库存不足");
        c.setStock(before - dto.getQuantity());
        componentService.updateById(c);
        recordService.recordOut(dto.getComponentId(), c.getBrand() + " " + c.getModel(),
                dto.getQuantity(), before, c.getStock(), dto.getRemark());
        return ApiResponse.success(null);
    }

    /** 库存变更记录 */
    @GetMapping("/records")
    public ApiResponse<List<InventoryRecord>> records(@RequestParam(defaultValue = "50") int limit) {
        return ApiResponse.success(recordService.getRecent(limit));
    }
}
