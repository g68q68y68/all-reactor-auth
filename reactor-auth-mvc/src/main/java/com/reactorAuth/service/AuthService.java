package com.reactorAuth.service;

import com.reactorAuth.dto.LoginRequest;
import com.reactorAuth.dto.LoginResponse;
import com.reactorAuth.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse register(RegisterRequest request);
    void logout();
}
