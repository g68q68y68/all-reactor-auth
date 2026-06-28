package com.reactorAuth.exception;

import com.reactorAuth.dto.Result;
import com.reactorAuth.dto.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ========== 参数校验异常 ==========

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Result<Void>> handleValidationException(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", message);
        return Mono.just(Result.error(ResultCode.BAD_REQUEST, message));
    }

    // ========== 认证异常 ==========

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<Result<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("认证失败: 用户名或密码错误");
        return Mono.just(Result.error(ResultCode.UNAUTHORIZED, "用户名或密码错误"));
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<Result<Void>> handleAuthenticationException(CustomAuthenticationException ex) {
        log.warn("认证异常: {}", ex.getMessage());
        return Mono.just(Result.error(ResultCode.UNAUTHORIZED, ex.getMessage()));
    }

    // ========== 权限异常 ==========

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Result<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return Mono.just(Result.error(ResultCode.FORBIDDEN));
    }

    // ========== 业务异常 ==========

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Result<Void>> handleBusinessException(BusinessException ex) {
        log.warn("业务异常: [{}] {}", ex.getResultCode().getCode(), ex.getMessage());
        return Mono.just(Result.error(ex.getResultCode(), ex.getMessage()));
    }

    // ========== 兜底异常 ==========

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Result<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return Mono.just(Result.error(ResultCode.INTERNAL_ERROR));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Result<Void>> handleException(Exception ex) {
        log.error("未知异常: {}", ex.getMessage(), ex);
        return Mono.just(Result.error(ResultCode.INTERNAL_ERROR));
    }
}
