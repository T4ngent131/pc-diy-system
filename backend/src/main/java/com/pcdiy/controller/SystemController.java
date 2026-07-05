package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SystemController {

    /** 健康检查 + 系统信息 */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("uptime", mx.getUptime());
        info.put("jvm", mx.getVmName() + " " + mx.getVmVersion());
        return ApiResponse.success(info);
    }
}
