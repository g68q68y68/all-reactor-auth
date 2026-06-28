package com.reactorAuth.service;

import com.reactorAuth.dto.LoginRequest;
import com.reactorAuth.dto.LoginResponse;
import com.reactorAuth.dto.RegisterRequest;
import com.reactorAuth.dto.ResultCode;
import com.reactorAuth.entity.User;
import com.reactorAuth.exception.BusinessException;
import com.reactorAuth.repository.PermissionRepository;
import com.reactorAuth.repository.RoleRepository;
import com.reactorAuth.repository.UserRepository;
import com.reactorAuth.security.CustomUserDetails;
import com.reactorAuth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        return authenticationManager.authenticate(authentication)
                .flatMap(authResult -> {
                    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

                    // 更新最后登录时间
                    return userRepository.updateLastLoginTime(userDetails.getUserId())
                            .then(Mono.zip(
                                    tokenProvider.generateToken(authResult),
                                    tokenProvider.generateRefreshToken(authResult)
                            ))
                            .flatMap(tokens -> {
                                String accessToken = tokens.getT1();
                                String refreshToken = tokens.getT2();

                                return buildLoginResponse(userDetails, accessToken, refreshToken);
                            });
                })
                .doOnError(error -> log.error("登录失败: {}", error.getMessage()));
    }

    @Transactional
    public Mono<LoginResponse> register(RegisterRequest registerRequest) {
        log.info("用户注册: {}", registerRequest.getUsername());

        // 检查用户名是否存在
        return userRepository.existsByUsername(registerRequest.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(ResultCode.USERNAME_EXISTS));
                    }
                    return userRepository.existsByEmail(registerRequest.getEmail());
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(ResultCode.EMAIL_EXISTS));
                    }

                    // 创建新用户
                    User user = User.builder()
                            .username(registerRequest.getUsername())
                            .password(passwordEncoder.encode(registerRequest.getPassword()))
                            .email(registerRequest.getEmail())
                            .phone(registerRequest.getPhone())
                            .fullName(registerRequest.getFullName())
                            .status(1)
                            .isAccountNonExpired(true)
                            .isAccountNonLocked(true)
                            .isCredentialsNonExpired(true)
                            .isEnabled(true)
                            .build();

                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                // 分配默认角色（普通用户）
                                return roleRepository.findByCode("ROLE_USER")
                                        .collectList()
                                        .flatMap(roles -> {
                                            if (!roles.isEmpty()) {
                                                // 这里需要插入user_roles关联表
                                                // 为了简化，此处省略关联表插入
                                            }
                                            return Mono.just(savedUser);
                                        });
                            })
                            .flatMap(savedUser -> {
                                // 自动登录
                                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        savedUser.getUsername(),
                                        registerRequest.getPassword()
                                );

                                return authenticationManager.authenticate(authentication)
                                        .flatMap(authResult -> {
                                            CustomUserDetails userDetails =
                                                    (CustomUserDetails) authResult.getPrincipal();

                                            return Mono.zip(
                                                    tokenProvider.generateToken(authResult),
                                                    tokenProvider.generateRefreshToken(authResult)
                                            ).flatMap(tokens -> {
                                                String accessToken = tokens.getT1();
                                                String refreshToken = tokens.getT2();

                                                return buildLoginResponse(
                                                        userDetails,
                                                        accessToken,
                                                        refreshToken
                                                );
                                            });
                                        });
                            });
                });
    }

    public Mono<LoginResponse> refreshToken(String refreshToken) {
        log.info("刷新Token");

        return tokenProvider.validateToken(refreshToken)
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new BusinessException(ResultCode.TOKEN_INVALID));
                    }

                    return tokenProvider.getUsernameFromToken(refreshToken)
                            .flatMap(username ->
                                userRepository.findByUsername(username)
                                        .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")))
                            )
                            .flatMap(user -> {
                                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        user.getUsername(),
                                        null,
                                        null
                                );

                                return Mono.zip(
                                        tokenProvider.generateToken(authentication),
                                        tokenProvider.generateRefreshToken(authentication)
                                ).flatMap(tokens -> {
                                    return roleRepository.findRolesByUserId(user.getId())
                                            .collectList()
                                            .zipWith(
                                                    permissionRepository.findPermissionsByUserId(user.getId())
                                                            .collectList()
                                            )
                                            .map(tuple -> {
                                                Set<String> roles = tuple.getT1().stream()
                                                        .map(role -> role.getCode())
                                                        .collect(Collectors.toSet());
                                                Set<String> permissions = tuple.getT2().stream()
                                                        .map(permission -> permission.getCode())
                                                        .collect(Collectors.toSet());

                                                CustomUserDetails userDetails = CustomUserDetails.fromUser(
                                                        user, roles, permissions
                                                );

                                                return userDetails;
                                            })
                                            .flatMap(userDetails ->
                                                buildLoginResponse(
                                                        userDetails,
                                                        tokens.getT1(),
                                                        tokens.getT2()
                                                )
                                            );
                                });
                            });
                });
    }

    private Mono<LoginResponse> buildLoginResponse(CustomUserDetails userDetails,
                                                    String accessToken,
                                                    String refreshToken) {
        return Mono.just(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(userDetails.getUserId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .phone(userDetails.getPhone())
                        .fullName(userDetails.getFullName())
                        .roles(userDetails.getRoles())
                        .permissions(userDetails.getPermissions())
                        .build())
                .build());
    }
}
