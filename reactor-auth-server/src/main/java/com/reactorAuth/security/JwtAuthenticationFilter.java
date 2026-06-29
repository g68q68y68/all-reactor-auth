package com.reactorAuth.security;

import com.reactorAuth.config.WhitelistProperties;
import com.reactorAuth.dto.Result;
import com.reactorAuth.dto.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider tokenProvider;
    private final WhitelistProperties whitelistProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 白名单路径直接放行
        if (whitelistProperties.isWhiteListPath(path)) {
            return chain.filter(exchange);
        }

        // 获取 Token
        String token = getTokenFromRequest(request);
        if (token == null) {
            log.debug("请求没有 Token: {}", path);
            return chain.filter(exchange);
        }

        // 【只验证 Token 有效性】
        return tokenProvider.validateToken(token)
                .flatMap(valid -> {
                    if (!valid) {
                        log.warn("Token 无效: {}", path);
                        return writeErrorResponse(exchange, ResultCode.TOKEN_INVALID);
                    }
                    
                    log.debug("Token 验证通过: {}", path);
                    return chain.filter(exchange);
                })
                .onErrorResume(ExpiredJwtException.class, e -> {
                    log.warn("Token 已过期: {}", path);
                    return writeErrorResponse(exchange, ResultCode.TOKEN_EXPIRED);
                })
                .onErrorResume(e -> {
                    log.warn("Token 验证异常: {}", e.getMessage());
                    return writeErrorResponse(exchange, ResultCode.TOKEN_INVALID);
                });
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, ResultCode resultCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            Result<Void> result = Result.error(resultCode);
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("写入错误响应失败: {}", e.getMessage());
            return response.setComplete();
        }
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}