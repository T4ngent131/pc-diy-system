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

    @PostMapping("/in")
    public ApiResponse<Void> stockIn(@RequestBody InventoryDTO dto) {
        String validationError = validateInventoryRequest(dto);
        if (validationError != null) return ApiResponse.error(400, validationError);

        Component component = componentService.getById(dto.getComponentId());
        if (component == null) return ApiResponse.error(404, "Component not found");

        int before = component.getStock();
        component.setStock(before + dto.getQuantity());
        componentService.updateById(component);
        recordService.recordIn(dto.getComponentId(), component.getBrand() + " " + component.getModel(),
                dto.getQuantity(), before, component.getStock(), dto.getRemark());
        return ApiResponse.success(null);
    }

    @PostMapping("/out")
    public ApiResponse<Void> stockOut(@RequestBody InventoryDTO dto) {
        String validationError = validateInventoryRequest(dto);
        if (validationError != null) return ApiResponse.error(400, validationError);

        Component component = componentService.getById(dto.getComponentId());
        if (component == null) return ApiResponse.error(404, "Component not found");

        int before = component.getStock();
        if (before < dto.getQuantity()) return ApiResponse.error(400, "Insufficient stock");

        component.setStock(before - dto.getQuantity());
        componentService.updateById(component);
        recordService.recordOut(dto.getComponentId(), component.getBrand() + " " + component.getModel(),
                dto.getQuantity(), before, component.getStock(), dto.getRemark());
        return ApiResponse.success(null);
    }

    @GetMapping("/records")
    public ApiResponse<List<InventoryRecord>> records(@RequestParam(defaultValue = "50") int limit) {
        return ApiResponse.success(recordService.getRecent(Math.max(1, Math.min(limit, 200))));
    }

    private String validateInventoryRequest(InventoryDTO dto) {
        if (dto.getComponentId() == null || dto.getComponentId().isBlank()) {
            return "Component id is required";
        }
        if (dto.getQuantity() <= 0) {
            return "Quantity must be greater than 0";
        }
        return null;
    }
}
