package com.reactorAuth.repository;

import com.reactorAuth.entity.RoleMenu;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoleMenuRepository extends R2dbcRepository<RoleMenu, Long> {

    @Query("SELECT menu_id FROM role_menus WHERE role_id = :roleId")
    Flux<Long> findMenuIdsByRoleId(Long roleId);

    @Modifying
    @Query("DELETE FROM role_menus WHERE role_id = :roleId")
    Mono<Void> deleteByRoleId(Long roleId);

    @Modifying
    @Query("DELETE FROM role_menus WHERE menu_id = :menuId")
    Mono<Void> deleteByMenuId(Long menuId);
}
