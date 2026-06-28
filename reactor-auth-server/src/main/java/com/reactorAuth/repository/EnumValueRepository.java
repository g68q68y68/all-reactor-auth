package com.reactorAuth.repository;

import com.reactorAuth.entity.EnumValue;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EnumValueRepository extends R2dbcRepository<EnumValue, Long>, PageableRepository<EnumValue> {

    @Query("SELECT * FROM enum_values WHERE type_id = :typeId ORDER BY sort_order ASC")
    Flux<EnumValue> findByTypeId(Long typeId);

    // ========== 分页查询 ==========

    @Query("SELECT COUNT(*) FROM enum_values")
    Mono<Long> count();

    @Query("SELECT * FROM enum_values ORDER BY type_id ASC, sort_order ASC LIMIT :limit OFFSET :offset")
    Flux<EnumValue> findPage(int limit, long offset);
}
