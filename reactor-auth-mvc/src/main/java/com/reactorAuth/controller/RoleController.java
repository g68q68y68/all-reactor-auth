package com.reactorAuth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.Role;
import com.reactorAuth.mapper.RoleMapper;
import com.reactorAuth.mapper.RoleMenuMapper;
import com.reactorAuth.mapper.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @GetMapping
    public Result<PageResult<Role>> page(@RequestParam(defaultValue="0") int page,
                                         @RequestParam(defaultValue="10") int size) {
        Page<Role> p = roleMapper.selectPage(new Page<>(page+1, size),
                new LambdaQueryWrapper<Role>().orderByDesc(Role::getId));
        return Result.success(PageResult.of(p));
    }

    @GetMapping("/{id}")
    public Result<Role> findById(@PathVariable Long id) { return Result.success(roleMapper.selectById(id)); }

    @PostMapping public Result<Role> create(@RequestBody Role r) { r.setId(null); roleMapper.insert(r); return Result.success(r); }

    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role r) { r.setId(id); roleMapper.updateById(r); return Result.success(roleMapper.selectById(id)); }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { roleMapper.deleteById(id); return Result.success(); }

    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getMenus(@PathVariable Long roleId) { return Result.success(roleMenuMapper.findMenuIdsByRoleId(roleId)); }

    @PutMapping("/{roleId}/menus")
    @Transactional
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null) menuIds.forEach(mid -> {
            var rm = new com.reactorAuth.entity.RoleMenu(); rm.setRoleId(roleId); rm.setMenuId(mid);
            roleMenuMapper.insert(rm);
        });
        return Result.success();
    }

    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long roleId) { return Result.success(rolePermissionMapper.findPermIdsByRoleId(roleId)); }

    @PutMapping("/{roleId}/permissions")
    @Transactional
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permIds != null) permIds.forEach(pid -> {
            var rp = new com.reactorAuth.entity.RolePermission(); rp.setRoleId(roleId); rp.setPermissionId(pid);
            rolePermissionMapper.insert(rp);
        });
        return Result.success();
    }
}
