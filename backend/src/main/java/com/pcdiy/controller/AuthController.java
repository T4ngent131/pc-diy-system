package com.pcdiy.controller;

import com.pcdiy.dto.ApiResponse;
import com.pcdiy.dto.AuthRequest;
import com.pcdiy.dto.AuthResponse;
import com.pcdiy.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody AuthRequest request) {
        try {
            return ApiResponse.success("Registered", authService.register(request));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Register failed", e);
            return ApiResponse.error(500, "Auth service error: " + e.getClass().getSimpleName());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            return ApiResponse.success("Logged in", authService.login(request));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Login failed", e);
            return ApiResponse.error(500, "Auth service error: " + e.getClass().getSimpleName());
        }
    }
}
