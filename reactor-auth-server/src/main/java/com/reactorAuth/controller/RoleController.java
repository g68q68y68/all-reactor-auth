package com.reactorAuth.controller;

import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.Role;
import com.reactorAuth.entity.RolePermission;
import com.reactorAuth.repository.RolePermissionRepository;
import com.reactorAuth.service.RoleMenuService;
import com.reactorAuth.service.RoleService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController extends BaseController<Role, RoleService> {

    private final RoleMenuService roleMenuService;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleController(RoleService roleService,
                          RoleMenuService roleMenuService,
                          RolePermissionRepository rolePermissionRepository) {
        super(roleService);
        this.roleMenuService = roleMenuService;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    /** 角色已分配的菜单 ID */
    @GetMapping("/{roleId}/menus")
    public Mono<Result<List<Long>>> getRoleMenus(@PathVariable Long roleId) {
        return roleMenuService.getMenuIdsByRoleId(roleId)
                .collectList().map(Result::success);
    }

    /** 批量设置角色菜单 */
    @PutMapping("/{roleId}/menus")
    public Mono<Result<Void>> assignRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        return roleMenuService.assignMenus(roleId, menuIds).thenReturn(Result.success());
    }

    /** 角色已分配的权限 ID */
    @GetMapping("/{roleId}/permissions")
    public Mono<Result<List<Long>>> getRolePermissions(@PathVariable Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId)
                .map(RolePermission::getPermissionId)
                .collectList().map(Result::success);
    }

    /** 批量设置角色权限 */
    @PutMapping("/{roleId}/permissions")
    public Mono<Result<Void>> assignRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permIds) {
        return rolePermissionRepository.deleteByRoleId(roleId)
                .then(Flux.fromIterable(permIds != null ? permIds : List.<Long>of())
                        .flatMap(pid -> rolePermissionRepository.save(
                                RolePermission.builder().roleId(roleId).permissionId(pid).build()))
                        .then())
                .thenReturn(Result.success());
    }
}
