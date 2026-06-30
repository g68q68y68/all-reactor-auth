package com.reactorAuth.repository;

import com.reactorAuth.entity.RolePermission;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RolePermissionRepository extends R2dbcRepository<RolePermission, Long> {

    @Query("SELECT * FROM sys_role_permissions WHERE role_id = :roleId")
    Flux<RolePermission> findByRoleId(Long roleId);

    @Modifying
    @Query("DELETE FROM sys_role_permissions WHERE role_id = :roleId")
    Mono<Void> deleteByRoleId(Long roleId);
}
