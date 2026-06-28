package com.reactorAuth.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactorAuth.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

/**
 * 权限缓存。Redis 存 JSON 列表 [{"method":"GET", "url":"/api/admin/**", "roles":"ROLE_ADMIN"}, ...]
 * 支持 Ant 路径匹配。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.security.url-auth.enabled", havingValue = "true")
public class PermissionCacheService {

    private static final String CACHE_KEY = "perm:list";
    private static final Duration TTL = Duration.ofHours(24);
    private static final AntPathMatcher matcher = new AntPathMatcher();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final PermissionRepository permissionRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        refresh().subscribe(
                v -> log.info("权限缓存初始化完成"),
                e -> log.warn("权限缓存初始化失败（Redis 可能未启动）: {}", e.getMessage())
        );
    }

    public Mono<Void> refresh() {
        return permissionRepository.findAllWithRoles()
                .collectList()
                .flatMap(rows -> {
                    try {
                        // 按 method+url 分组，合并授权标识（角色码 + 权限码）
                        Map<String, Set<String>> map = new LinkedHashMap<>();
                        for (var row : rows) {
                            String key = row.getMethod() + "|" + row.getUrl();
                            map.computeIfAbsent(key, k -> new LinkedHashSet<>())
                                    .add(row.getAuthority());
                        }
                        List<Map<String, String>> list = new ArrayList<>();
                        for (var entry : map.entrySet()) {
                            String[] parts = entry.getKey().split("\\|", 2);
                            list.add(Map.of(
                                    "method", parts[0],
                                    "url", parts[1],
                                    "roles", String.join(",", entry.getValue())
                            ));
                        }
                        String json = objectMapper.writeValueAsString(list);
                        return redisTemplate.opsForValue().set(CACHE_KEY, json, TTL)
                                .doOnSuccess(v -> log.info("权限缓存刷新完成，共 {} 条规则", list.size()))
                                .then();
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    /**
     * 检查权限。
     * @return true=允许, false=拒绝
     */
    public Mono<Boolean> isAllowed(String method, String path, Set<String> authorities) {
        if (authorities == null || authorities.isEmpty()) return Mono.just(true);

        return redisTemplate.opsForValue().get(CACHE_KEY)
                .defaultIfEmpty("[]")
                .map(json -> {
                    try {
                        List<Map<String, String>> rules = objectMapper.readValue(
                                json, new TypeReference<List<Map<String, String>>>() {});

                        boolean matchedAnyRule = false;

                        for (Map<String, String> rule : rules) {
                            String m = rule.get("method");
                            String pattern = rule.get("url");
                            if (m == null || pattern == null) continue;
                            if (!method.equalsIgnoreCase(m)) continue;
                            if (!matcher.match(pattern, path)) continue;

                            // 匹配到规则 → 检查用户拥有的授权标识（角色+权限）
                            matchedAnyRule = true;
                            String allowed = rule.get("roles");
                            if (allowed == null || allowed.isEmpty()) return true;
                            for (String auth : authorities) {
                                if (allowed.contains(auth)) return true;
                            }
                        }

                        // 没有匹配到任何规则 → 放行（未受限 URL）
                        // 匹配到规则但角色不满足 → 拒绝
                        return !matchedAnyRule;
                    } catch (Exception e) {
                        log.warn("权限缓存解析失败: {}", e.getMessage());
                        return true;
                    }
                });
    }
}
