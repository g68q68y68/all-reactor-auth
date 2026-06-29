package com.reactorAuth.utils;

import com.reactorAuth.dto.UserInfo;
import com.reactorAuth.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户工具类
 * 用于从 ReactiveSecurityContextHolder 中获取当前登录用户信息
 */
@Slf4j
@Component
public class LoginUserUtil {

    /**
     * 获取当前登录用户的 Authentication
     *
     * @return Mono<Authentication>
     */
    public Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnError(e -> log.error("获取Authentication失败: {}", e.getMessage()));
    }

    /**
     * 获取当前登录用户的 CustomUserDetails
     *
     * @return Mono<CustomUserDetails>
     */
    public Mono<CustomUserDetails> getUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> (CustomUserDetails) principal)
                .switchIfEmpty(Mono.error(new RuntimeException("当前用户未登录或类型不正确")))
                .doOnError(e -> log.error("获取UserDetails失败: {}", e.getMessage()));
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return Mono<String>
     */
    public Mono<String> getUsername() {
        return getAuthentication()
                .map(Authentication::getName)
                .switchIfEmpty(Mono.just("anonymous"));
    }

    /**
     * 获取当前登录用户的ID
     *
     * @return Mono<Long>
     */
    public Mono<Long> getUserId() {
        return getUserDetails()
                .map(CustomUserDetails::getUserId)
                .switchIfEmpty(Mono.error(new RuntimeException("获取用户ID失败")));
    }

    /**
     * 获取当前登录用户的邮箱
     *
     * @return Mono<String>
     */
    public Mono<String> getEmail() {
        return getUserDetails()
                .map(CustomUserDetails::getEmail)
                .switchIfEmpty(Mono.just(""));
    }

    /**
     * 获取当前登录用户的权限列表
     *
     * @return Mono<List<String>>
     */
    public Mono<List<String>> getPermissions() {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .switchIfEmpty(Mono.just(Collections.emptyList()));
    }

    /**
     * 获取当前登录用户的角色列表（不含 ROLE_ 前缀）
     *
     * @return Mono<List<String>>
     */
    public Mono<Set<String>> getRoles() {
        return getPermissions()
                .map(permissions -> permissions.stream()
                        .filter(permission -> permission.startsWith("ROLE_"))
                        .map(permission -> permission.substring(5)) // 去掉 ROLE_ 前缀
                        .collect(Collectors.toSet()));
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return Mono<Boolean>
     */
    public Mono<Boolean> isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .defaultIfEmpty(false);
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param role 角色名（不含 ROLE_ 前缀）
     * @return Mono<Boolean>
     */
    public Mono<Boolean> hasRole(String role) {
        return getRoles()
                .map(roles -> roles.contains(role));
    }

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限名
     * @return Mono<Boolean>
     */
    public Mono<Boolean> hasPermission(String permission) {
        return getPermissions()
                .map(permissions -> permissions.contains(permission));
    }

    /**
     * 检查当前用户是否拥有指定角色（包含 ROLE_ 前缀）
     *
     * @param role 角色名（含 ROLE_ 前缀）
     * @return Mono<Boolean>
     */
    public Mono<Boolean> hasRoleWithPrefix(String role) {
        return getPermissions()
                .map(permissions -> permissions.contains(role));
    }

    /**
     * 获取当前用户的详细信息（完整对象）
     *
     * @return Mono<UserInfo>
     */
    public Mono<UserInfo> getUserInfo() {
        return Mono.zip(
                getUserId(),
                getUsername(),
                getEmail(),
                getPermissions(),
                getRoles(),
                isAuthenticated()
        ).map(tuple -> UserInfo.builder()
                .userId(tuple.getT1())
                .username(tuple.getT2())
                .email(tuple.getT3())
                .permissions(tuple.getT4())
                .roles(tuple.getT5())
                .authenticated(tuple.getT6())
                .build());
    }

    /**
     * 获取当前用户的详细信息（直接返回 UserDetails 对象）
     * 适用于需要完整 UserDetails 的场景
     *
     * @return Mono<UserDetails>
     */
    public Mono<UserDetails> getUserDetailsAsUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> (UserDetails) principal);
    }

    /**
     * 获取当前用户ID（同步方式，适用于已有 Mono 上下文的场景）
     * 注意：此方法必须在响应式上下文中调用
     *
     * @return Long
     * @throws RuntimeException 如果获取失败
     */
    public Long getUserIdSync() {
        return getUserId().block();
    }

    /**
     * 获取当前用户名（同步方式）
     */
    public String getUsernameSync() {
        return getUsername().block();
    }
}