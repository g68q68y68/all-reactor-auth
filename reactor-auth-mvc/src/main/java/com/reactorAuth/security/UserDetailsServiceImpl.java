package com.reactorAuth.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reactorAuth.entity.User;
import com.reactorAuth.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) throw new UsernameNotFoundException("用户不存在: " + username);

        Set<String> roles = new HashSet<>(userRoleMapper.findRoleCodesByUserId(user.getId()));
        Set<String> perms = rolePermissionMapper.findPermissionsByUserId(user.getId())
                .stream().map(p -> p.getCode()).collect(java.util.stream.Collectors.toSet());

        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getFullName(), roles, perms,
                user.getIsEnabled() != null && user.getIsEnabled());
    }
}
