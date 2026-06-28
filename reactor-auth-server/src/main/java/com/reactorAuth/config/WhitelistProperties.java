package com.reactorAuth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security.whitelist")
@PropertySource("classpath:whiteList.properties")
public class WhitelistProperties {

    /**
     * 白名单路径列表，支持 Ant 风格的通配符，如 /auth/**, /public/**
     */
    private List<String> paths = Arrays.asList(
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断请求路径是否在白名单中
     */
    public boolean isWhiteListPath(String requestPath) {
        return paths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }
}
