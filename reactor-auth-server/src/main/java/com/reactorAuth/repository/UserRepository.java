package com.reactorAuth.repository;

import com.reactorAuth.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long>, PageableRepository<User> {

    @Query("SELECT * FROM users WHERE username = :username")
    Mono<User> findByUsername(String username);

    @Query("SELECT * FROM users WHERE email = :email")
    Mono<User> findByEmail(String email);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    Mono<Boolean> existsByUsername(String username);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);

    @Query("UPDATE users SET last_login_time = NOW() WHERE id = :userId")
    Mono<Void> updateLastLoginTime(Long userId);

    /** 批量查用户的角色编码 */
    @Query("""
        SELECT ur.user_id, r.code FROM user_roles ur
        INNER JOIN roles r ON r.id = ur.role_id
        WHERE ur.user_id IN (:userIds)
    """)
    Flux<UserRoleDto> findRolesByUserIds(List<Long> userIds);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM users")
    Mono<Long> count();

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT :limit OFFSET :offset")
    Flux<User> findPage(int limit, long offset);
}
