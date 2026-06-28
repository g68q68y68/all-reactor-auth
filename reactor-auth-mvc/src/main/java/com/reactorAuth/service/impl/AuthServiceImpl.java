package com.reactorAuth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reactorAuth.dto.LoginRequest;
import com.reactorAuth.dto.LoginResponse;
import com.reactorAuth.dto.RegisterRequest;
import com.reactorAuth.entity.User;
import com.reactorAuth.entity.UserRole;
import com.reactorAuth.exception.BusinessException;
import com.reactorAuth.mapper.*;
import com.reactorAuth.security.JwtTokenProvider;
import com.reactorAuth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        List<String> roles = userRoleMapper.findRoleCodesByUserId(user.getId());
        List<String> perms = rolePermissionMapper.findPermissionsByUserId(user.getId())
                .stream().map(p -> p.getCode()).toList();

        String token = jwtTokenProvider.generateToken(auth);
        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setUserId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setFullName(user.getFullName());
        info.setRoles(roles);
        info.setPermissions(perms);

        LoginResponse resp = new LoginResponse();
        resp.setAccessToken(token);
        resp.setTokenType("Bearer");
        resp.setExpiresIn(86400);
        resp.setUserInfo(info);
        return resp;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername())))
            throw new BusinessException(40901, "用户名已存在");
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getEmail, req.getEmail())))
            throw new BusinessException(40902, "邮箱已被注册");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setFullName(req.getFullName());
        user.setStatus(1);
        user.setIsEnabled(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        userMapper.insert(user);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(req.getUsername());
        loginReq.setPassword(req.getPassword());
        return login(loginReq);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
