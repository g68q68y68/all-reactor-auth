package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.Permission;
import com.reactorAuth.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionMapper permissionMapper;

    @GetMapping
    public Result<PageResult<Permission>> page(@RequestParam(defaultValue="0") int page,
                                                @RequestParam(defaultValue="10") int size) {
        Page<Permission> p = permissionMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<Permission>().orderByDesc(Permission::getId));
        return Result.success(PageResult.of(p));
    }

    @GetMapping("/{id}")
    public Result<Permission> findById(@PathVariable Long id) { return Result.success(permissionMapper.selectById(id)); }

    @PostMapping public Result<Permission> create(@RequestBody Permission p) { p.setId(null); permissionMapper.insert(p); return Result.success(p); }

    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable Long id, @RequestBody Permission p) { p.setId(id); permissionMapper.updateById(p); return Result.success(permissionMapper.selectById(id)); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { permissionMapper.deleteById(id); return Result.success(); }
}
