package com.reactorAuth.security;

import com.reactorAuth.entity.Permission;
import com.reactorAuth.entity.Role;
import com.reactorAuth.repository.PermissionRepository;
import com.reactorAuth.repository.RoleRepository;
import com.reactorAuth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("加载用户信息: {}", username);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")))
                .flatMap(user -> {
                    // 查询用户角色
                    Mono<Set<String>> rolesMono = roleRepository.findRolesByUserId(user.getId())
                            .map(Role::getCode)
                            .collectList()
                            .map(HashSet::new);

                    // 查询用户权限
                    Mono<Set<String>> permissionsMono = permissionRepository.findPermissionsByUserId(user.getId())
                            .map(Permission::getCode)
                            .collectList()
                            .map(HashSet::new);

                    // 组合结果
                    return Mono.zip(rolesMono, permissionsMono)
                            .map(tuple -> {
                                Set<String> roles = tuple.getT1();
                                Set<String> permissions = tuple.getT2();

                                log.info("用户 {} 的角色: {}, 权限: {}", username, roles, permissions);

                                return (UserDetails) CustomUserDetails.fromUser(user, roles, permissions);
                            });
                })
                .doOnError(error -> log.error("加载用户失败: {}", error.getMessage()));
    }
}
