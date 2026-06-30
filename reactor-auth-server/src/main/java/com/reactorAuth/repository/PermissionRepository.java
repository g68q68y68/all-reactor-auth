package com.reactorAuth.repository;

import com.reactorAuth.entity.Permission;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PermissionRepository extends R2dbcRepository<Permission, Long>, PageableRepository<Permission> {

    @Query("""
        SELECT DISTINCT p.* FROM sys_permissions p
        INNER JOIN sys_role_permissions rp ON rp.permission_id = p.id
        INNER JOIN sys_user_roles ur ON ur.role_id = rp.role_id
        WHERE ur.user_id = :userId AND p.status = 1
    """)
    Flux<Permission> findPermissionsByUserId(Long userId);

    @Query("""
        SELECT DISTINCT p.* FROM sys_permissions p
        INNER JOIN sys_role_permissions rp ON rp.permission_id = p.id
        WHERE rp.role_id = :roleId
    """)
    Flux<Permission> findPermissionsByRoleId(Long roleId);

    /** 查询所有权限的 URL+Method 及其对应的授权标识（角色码 + 权限码），用于缓存 */
    @Query("""
        SELECT p.method, p.url, r.code FROM sys_permissions p
        INNER JOIN sys_role_permissions rp ON rp.permission_id = p.id
        INNER JOIN sys_roles r ON r.id = rp.role_id
        WHERE p.status = 1 AND p.url IS NOT NULL
        UNION
        SELECT p.method, p.url, p.code FROM sys_permissions p
        WHERE p.status = 1 AND p.url IS NOT NULL
    """)
    Flux<com.reactorAuth.dto.PermissionUrlRole> findAllWithRoles();

    @Query("""
        SELECT p.* FROM sys_permissions p
        WHERE p.url = :url AND p.method = :method AND p.status = 1
    """)
    Flux<Permission> findByUrlAndMethod(String url, String method);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM sys_permissions")
    Mono<Long> count();

    @Query("SELECT * FROM sys_permissions ORDER BY id DESC LIMIT :limit OFFSET :offset")
    Flux<Permission> findPage(int limit, long offset);
}
