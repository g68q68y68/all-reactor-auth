package com.reactorAuth.repository;

import com.reactorAuth.entity.Menu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MenuRepository extends R2dbcRepository<Menu, Long>, PageableRepository<Menu> {

    @Query("SELECT * FROM sys_menus WHERE status = 1 ORDER BY sort_order ASC, id ASC")
    Flux<Menu> findAllEnabledMenus();

    @Query("SELECT * FROM sys_menus WHERE parent_id = :parentId AND status = 1 ORDER BY sort_order ASC")
    Flux<Menu> findChildrenByParentId(Long parentId);

    /**
     * 按用户角色查询菜单（非 ADMIN 用户）：
     * user → user_roles → role_menus → menus
     */
    @Query("""
        SELECT DISTINCT m.* FROM sys_menus m
        INNER JOIN sys_role_menus rm ON rm.menu_id = m.id
        INNER JOIN sys_user_roles ur ON ur.role_id = rm.role_id
        WHERE ur.user_id = :userId AND m.status = 1
        ORDER BY m.sort_order ASC, m.id ASC
    """)
    Flux<Menu> findMenusByUserId(Long userId);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM sys_menus")
    Mono<Long> count();

    @Query("SELECT * FROM sys_menus ORDER BY sort_order ASC, id ASC LIMIT :limit OFFSET :offset")
    Flux<Menu> findPage(int limit, long offset);
}
