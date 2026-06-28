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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // 获取Token
        String token = getTokenFromRequest(request);
        if (token == null) {
            return chain.filter(exchange);
        }

        // 解析Token：有效→设上下文；过期→40101；无效→40102
        return tokenProvider.getClaimsFromToken(token)
                .flatMap(claims -> {
                    // Token 有效，解析用户信息并设置安全上下文
                    Long userId = claims.get("userId", Long.class);
                    @SuppressWarnings("unchecked")
                    List<String> rolesList = (List<String>) claims.get("roles");
                    Set<String> roles = rolesList != null
                            ? rolesList.stream().collect(Collectors.toSet())
                            : Set.of();

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId.toString(), null, authorities);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                .onErrorResume(ExpiredJwtException.class, e -> {
                    log.warn("Token 已过期: {}", e.getMessage());
                    return writeErrorResponse(exchange, ResultCode.TOKEN_EXPIRED);
                })
                .onErrorResume(e -> {
                    log.warn("Token 无效: {}", e.getMessage());
                    return writeErrorResponse(exchange, ResultCode.TOKEN_INVALID);
                });
    }

    /**
     * 返回统一结构的 401 错误响应
     */
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
