package com.reactorAuth.repository;

import com.reactorAuth.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Long>, PageableRepository<Role> {

    @Query("""
        SELECT r.* FROM roles r
        INNER JOIN user_roles ur ON ur.role_id = r.id
        WHERE ur.user_id = :userId
    """)
    Flux<Role> findRolesByUserId(Long userId);

    @Query("SELECT r.* FROM roles r WHERE r.code = :code")
    Flux<Role> findByCode(String code);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM roles")
    Mono<Long> count();

    @Query("SELECT * FROM roles ORDER BY id DESC LIMIT :limit OFFSET :offset")
    Flux<Role> findPage(int limit, long offset);
}
