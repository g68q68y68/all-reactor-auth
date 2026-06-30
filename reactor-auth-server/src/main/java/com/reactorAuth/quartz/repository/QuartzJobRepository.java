package com.reactorAuth.quartz.repository;

import com.reactorAuth.quartz.entity.QuartzJob;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface QuartzJobRepository extends R2dbcRepository<QuartzJob, Long> {

    @Query("SELECT * FROM sys_quartz_jobs WHERE job_name = :jobName AND job_group = :jobGroup")
    Mono<QuartzJob> findByJobNameAndJobGroup(String jobName, String jobGroup);

    @Query("SELECT * FROM sys_quartz_jobs WHERE status = 1")
    Flux<QuartzJob> findAllEnabled();

    @Query("SELECT COUNT(*) FROM sys_quartz_jobs")
    Mono<Long> count();

    @Query("SELECT * FROM sys_quartz_jobs ORDER BY id DESC LIMIT :limit OFFSET :offset")
    Flux<QuartzJob> findPage(int limit, long offset);
}
