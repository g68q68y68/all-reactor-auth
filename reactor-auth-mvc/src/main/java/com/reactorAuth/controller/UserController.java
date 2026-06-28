package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.User;
import com.reactorAuth.mapper.UserMapper;
import com.reactorAuth.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<PageResult<User>> page(@RequestParam(defaultValue="0") int page,
                                         @RequestParam(defaultValue="10") int size) {
        Page<User> p = userMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<User>().orderByDesc(User::getId));
        PageResult<User> pr = PageResult.of(p);
        if (!pr.getRecords().isEmpty()) {
            List<Long> ids = pr.getRecords().stream().map(User::getId).toList();
            Map<Long, List<String>> roleMap = new HashMap<>();
            userRoleMapper.findRolesByUserIds(ids).forEach(row -> {
                Long userId = (Long) row[0];
                String code = (String) row[1];
                roleMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(code);
            });
            pr.getRecords().forEach(u -> u.setRoles(roleMap.getOrDefault(u.getId(), List.of())));
        }
        return Result.success(pr);
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User u = userMapper.selectById(id);
        if (u != null) u.setRoles(userRoleMapper.findRoleCodesByUserId(id));
        return Result.success(u);
    }

    @PostMapping
    public Result<User> create(@RequestBody User u) {
        u.setId(null);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userMapper.insert(u);
        return Result.success(u);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User u) {
        u.setId(id);
        if (u.getPassword() != null && !u.getPassword().isEmpty())
            u.setPassword(passwordEncoder.encode(u.getPassword()));
        else u.setPassword(null);
        userMapper.updateById(u);
        return Result.success(userMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { userMapper.deleteById(id); return Result.success(); }
}
