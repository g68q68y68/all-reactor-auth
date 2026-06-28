package com.reactorAuth.service;

import com.reactorAuth.entity.RoleMenu;
import com.reactorAuth.repository.RoleMenuRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;

    public RoleMenuService(RoleMenuRepository roleMenuRepository) {
        this.roleMenuRepository = roleMenuRepository;
    }

    /**
     * 获取角色已分配的菜单 ID 列表
     */
    public Flux<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuRepository.findMenuIdsByRoleId(roleId);
    }

    /**
     * 批量设置角色菜单权限（先删后插）
     */
    public Mono<Void> assignMenus(Long roleId, List<Long> menuIds) {
        return roleMenuRepository.deleteByRoleId(roleId)
                .then(Mono.defer(() -> {
                    if (menuIds == null || menuIds.isEmpty()) {
                        return Mono.empty();
                    }
                    List<RoleMenu> roleMenus = menuIds.stream()
                            .map(menuId -> RoleMenu.builder()
                                    .roleId(roleId)
                                    .menuId(menuId)
                                    .build())
                            .collect(Collectors.toList());
                    return roleMenuRepository.saveAll(roleMenus).then();
                }));
    }
}
