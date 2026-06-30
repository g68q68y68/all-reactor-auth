package com.reactorAuth.repository;

import com.reactorAuth.entity.AuditLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AuditLogRepository extends R2dbcRepository<AuditLog, Long> {

    @Query("SELECT COUNT(*) FROM sys_audit_logs")
    Mono<Long> count();

    @Query("SELECT * FROM sys_audit_logs ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    Flux<AuditLog> findPage(int limit, long offset);
}
