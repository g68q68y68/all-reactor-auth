package com.reactorAuth.repository;

import com.reactorAuth.entity.EnumType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EnumTypeRepository extends R2dbcRepository<EnumType, Long>, PageableRepository<EnumType> {

    @Query("SELECT * FROM sys_enum_types WHERE code = :code")
    Mono<EnumType> findByCode(String code);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM sys_enum_types")
    Mono<Long> count();

    @Query("SELECT * FROM sys_enum_types ORDER BY id ASC LIMIT :limit OFFSET :offset")
    Flux<EnumType> findPage(int limit, long offset);
}
