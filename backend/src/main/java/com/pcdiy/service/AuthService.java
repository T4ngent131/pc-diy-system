package com.pcdiy.service;

import com.pcdiy.dto.AuthRequest;
import com.pcdiy.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}
