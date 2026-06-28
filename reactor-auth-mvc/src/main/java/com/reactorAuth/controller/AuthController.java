package com.reactorAuth.controller;

import com.reactorAuth.dto.*;
import com.reactorAuth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(authService.register(req));
    }

    @PostMapping("/logout")
    public Result<Void> logout() { authService.logout(); return Result.success(); }
}
