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
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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



    public Mono<LoginResponse> login(LoginRequest loginRequest, ServerWebExchange exchange) {
        log.info("用户登录: {}", loginRequest.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        return authenticationManager.authenticate(authentication)
                .flatMap(authResult -> {
                    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
                    Long userId = userDetails.getUserId();

                    // 【关键】手动保存到 Session
                    return exchange.getSession()
                            .doOnNext(session -> log.info("1. 获取到 Session: {}", session.getId()))
                            .flatMap(session -> {
                                SecurityContext securityContext = new SecurityContextImpl(authResult);

                                // 存入 Session
                                session.getAttributes().put("SPRING_SECURITY_CONTEXT", securityContext);
                                log.info("2. 存入 SecurityContext 到 Session");
                                log.info("3. Session 属性: {}", session.getAttributes().keySet());

                                // 【必须】调用 save()
                                return session.save()
                                        .doOnSuccess(v -> log.info("4. ✅ Session 保存成功: {}", session.getId()))
                                        .doOnError(e -> log.error("5. ❌ Session 保存失败: {}", e.getMessage()));
                            })
                            .then(Mono.defer(() -> {
                                log.info("6. 开始更新登录时间");
                                return userRepository.updateLastLoginTime(userId)
                                        .doOnSuccess(v -> log.info("7. ✅ 更新登录时间成功"))
                                        .doOnError(e -> log.error("8. ❌ 更新登录时间失败: {}", e.getMessage()))
                                        .then(Mono.defer(() -> {
                                            log.info("9. 开始生成 Token");
                                            return Mono.zip(
                                                    tokenProvider.generateToken(authResult),
                                                    tokenProvider.generateRefreshToken(authResult)
                                            );
                                        }));
                            }))
                            .flatMap(tokens -> {
                                log.info("10. Token 生成成功");
                                return buildLoginResponse(
                                        userDetails,
                                        tokens.getT1(),
                                        tokens.getT2()
                                );
                            })
                            // 设置当前请求的上下文
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authResult));
                })
                .doOnSuccess(response -> log.info("✅ 登录成功: {}", loginRequest.getUsername()))
                .doOnError(error -> log.error("❌ 登录失败: {}", error.getMessage()));
    }

    public Mono<LoginResponse> register(RegisterRequest req) {
        return userRepository.existsByUsername(req.getUsername())
                .flatMap(exists -> {
                    if (exists) return Mono.error(new BusinessException(ResultCode.USERNAME_EXISTS));
                    return userRepository.existsByEmail(req.getEmail());
                })
                .flatMap(emailExists -> {
                    if (emailExists) return Mono.error(new BusinessException(ResultCode.EMAIL_EXISTS));
                    User user = User.builder()
                            .username(req.getUsername())
                            .password(passwordEncoder.encode(req.getPassword()))
                            .email(req.getEmail())
                            .phone(req.getPhone())
                            .fullName(req.getFullName())
                            .status(1).isEnabled(true)
                            .isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                            .build();
                    return userRepository.save(user);
                })
                .flatMap(user -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), req.getPassword());
                    return authenticationManager.authenticate(auth)
                            .flatMap(authResult -> tokenProvider.generateToken(authResult)
                                    .flatMap(token -> buildLoginResponse(
                                            (CustomUserDetails) authResult.getPrincipal(), token, "")));
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
