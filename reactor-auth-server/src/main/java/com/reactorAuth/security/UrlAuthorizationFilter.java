package com.reactorAuth.security;

import com.reactorAuth.config.UrlAuthProperties;
import com.reactorAuth.config.WhitelistProperties;
import com.reactorAuth.dto.Result;
import com.reactorAuth.dto.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 URL + Method 的权限验证过滤器。
 * 仅在 spring.security.url-auth.enabled=true 时生效。
 * 权限数据从 Redis 缓存读取（启动时从 DB 加载）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.security.url-auth.enabled", havingValue = "true")
public class UrlAuthorizationFilter implements WebFilter {

    private final UrlAuthProperties urlAuthProperties;
    private final WhitelistProperties whitelistProperties;
    private final PermissionCacheService cacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 未开启 → 跳过
        if (!urlAuthProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        // 白名单路径跳过
        if (whitelistProperties.isWhiteListPath(path)) {
            return chain.filter(exchange);
        }

        // 从 SecurityContext 获取当前用户认证信息
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    var auth = ctx.getAuthentication();
                    // 未认证用户不拦截——此时请求还未到达 Controller，由 @PreAuthorize 或后续过滤器处理
                    if (auth == null || !auth.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    // authorities 包含角色码（ROLE_ADMIN）+ 权限码（user:read）
                    Set<String> authorities = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    return cacheService.isAllowed(method, path, authorities)
                            .flatMap(allowed -> {
                                if (allowed) {
                                    return chain.filter(exchange);
                                }
                                log.warn("权限拒绝: {} {} authorities={}", method, path, authorities);
                                return writeForbidden(exchange);
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> writeForbidden(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            Result<Void> result = Result.error(ResultCode.FORBIDDEN);
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
