package com.pcdiy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String phone;
    private String role;
    private String token;
}
