package com.reactorAuth.service;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.entity.User;
import com.reactorAuth.repository.UserRepository;
import com.reactorAuth.repository.UserRoleDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService<User, UserRepository> {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository, "用户");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** 覆写分页：附加角色信息 */
    @Override
    public Mono<PageResult<User>> page(int page, int size) {
        return super.page(page, size).flatMap(pageResult -> {
            List<User> users = pageResult.getRecords();
            if (users.isEmpty()) return Mono.just(pageResult);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            return userRepository.findRolesByUserIds(userIds)
                    .collectList()
                    .map(roleDtos -> {
                        Map<Long, List<String>> roleMap = roleDtos.stream()
                                .collect(Collectors.groupingBy(
                                        UserRoleDto::getUserId,
                                        Collectors.mapping(UserRoleDto::getCode, Collectors.toList())
                                ));
                        users.forEach(u -> u.setRoles(roleMap.getOrDefault(u.getId(), new ArrayList<>())));
                        return pageResult;
                    });
        });
    }

    /** 覆写查单个：附加角色信息 */
    @Override
    public Mono<User> findById(Long id) {
        return super.findById(id).flatMap(user ->
                userRepository.findRolesByUserIds(List.of(id))
                        .collectList()
                        .map(roleDtos -> {
                            List<String> roles = roleDtos.stream()
                                    .map(UserRoleDto::getCode)
                                    .collect(Collectors.toList());
                            user.setRoles(roles);
                            return user;
                        })
        );
    }

    @Override
    protected void prepareCreate(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) user.setStatus(1);
        if (user.getIsEnabled() == null) user.setIsEnabled(true);
        if (user.getIsAccountNonExpired() == null) user.setIsAccountNonExpired(true);
        if (user.getIsAccountNonLocked() == null) user.setIsAccountNonLocked(true);
        if (user.getIsCredentialsNonExpired() == null) user.setIsCredentialsNonExpired(true);
    }

    @Override
    protected void mergeEntity(User existing, User incoming) {
        if (incoming.getEmail() != null) existing.setEmail(incoming.getEmail());
        if (incoming.getPhone() != null) existing.setPhone(incoming.getPhone());
        if (incoming.getFullName() != null) existing.setFullName(incoming.getFullName());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
        if (incoming.getPassword() != null && !incoming.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(incoming.getPassword()));
        }
    }
}
