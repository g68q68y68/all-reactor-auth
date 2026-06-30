package com.reactorAuth.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactorAuth.annotation.AuditLog;

import com.reactorAuth.repository.AuditLogRepository;
import com.reactorAuth.utils.LoginUserUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);
    private final AuditLogRepository repository;
    private final LoginUserUtil loginUserUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, AuditLog auditLog) {
        // 1. 先收集请求信息（此时还没执行方法）
        Object[] args = point.getArgs();
        String params = extractParams(args);
        String ip = extractIp(args);
        var method = ((MethodSignature) point.getSignature()).getMethod();
        String url = point.getTarget().getClass().getSimpleName() + "." + method.getName();

        // 2. 执行目标方法
        try {
            @SuppressWarnings("unchecked")
            Mono<Object> result = (Mono<Object>) point.proceed();

            return result
                    .flatMap(response -> {
                        // 3. 成功后获取用户（登录成功此时已有 SecurityContext）
                        return loginUserUtil.getUserDetails()
                                .map(user -> buildLog(user.getUserId(), user.getUsername(), auditLog, url, ip, params, 1, null))
                                .onErrorResume(e -> Mono.just(
                                        buildLog(null, "anonymous", auditLog, url, ip, params, 1, null)))
                                .flatMap(logEntry -> {
                                    saveLog(logEntry);
                                    return Mono.just(response);
                                });
                    })
                    .onErrorResume(error -> {
                        // 4. 失败后也记录
                        return loginUserUtil.getUserDetails()
                                .map(user -> buildLog(user.getUserId(), user.getUsername(), auditLog, url, ip, params, 0, error.getMessage()))
                                .onErrorResume(e -> Mono.just(
                                        buildLog(null, "anonymous", auditLog, url, ip, params, 0, error.getMessage())))
                                .flatMap(logEntry -> {
                                    saveLog(logEntry);
                                    return Mono.error(error); // 继续抛出
                                });
                    });
        } catch (Throwable e) {
            saveLog(buildLog(null, "anonymous", auditLog, url, ip, params, 0, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private com.reactorAuth.entity.AuditLog buildLog(Long userId, String username, AuditLog annotation,
                                                       String url, String ip, String params, int status, String errorMsg) {
        return com.reactorAuth.entity.AuditLog.builder()
                .userId(userId).username(username)
                .module(annotation.module()).action(annotation.action())
                .description(annotation.description())
                .url(url)
                .ip(ip).params(truncate(params, 1000))
                .status(status).errorMsg(truncate(errorMsg, 500))
                .createdAt(LocalDateTime.now()).build();
    }

    private String extractParams(Object[] args) {
        try {
            for (Object arg : args) {
                if (!(arg instanceof ServerWebExchange)) {
                    String json = objectMapper.writeValueAsString(arg);
                    return json.length() > 1000 ? json.substring(0, 1000) + "..." : json;
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String extractIp(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof ServerWebExchange exchange) {
                ServerHttpRequest req = exchange.getRequest();
                return req.getRemoteAddress() != null
                        ? req.getRemoteAddress().getAddress().getHostAddress() : "unknown";
            }
        }
        return "unknown";
    }

    private void saveLog(com.reactorAuth.entity.AuditLog log) {
        repository.save(log).subscribe(
                v -> {},
                e -> logger.error("保存操作日志失败: {}", e.getMessage())
        );
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
