package com.reactorAuth.controller;

import com.reactorAuth.dto.LoginRequest;
import com.reactorAuth.dto.LoginResponse;
import com.reactorAuth.dto.RegisterRequest;
import com.reactorAuth.dto.Result;
import com.reactorAuth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Result<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, ServerWebExchange exchange) {
        log.info("登录请求: {}", loginRequest.getUsername());
        return authService.login(loginRequest,exchange)
                .map(Result::success);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Result<LoginResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("注册请求: {}", registerRequest.getUsername());
        return authService.register(registerRequest)
                .map(Result::success);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Result<LoginResponse>> refreshToken(@RequestHeader("Authorization") String authorization) {
        String refreshToken = authorization.startsWith("Bearer ") ?
                authorization.substring(7) : authorization;
        log.info("刷新Token请求");
        return authService.refreshToken(refreshToken)
                .map(Result::success);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Result<Void>> logout() {
        log.info("登出请求");
        return Mono.just(Result.success());
    }
}
