package com.reactorAuth.service;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.ResultCode;
import com.reactorAuth.exception.BusinessException;
import com.reactorAuth.repository.PageableRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * 单表通用 Service 基类 —— 提供分页、增删改查模板方法
 *
 * @param <T> 实体类型
 * @param <R> Repository 类型（需同时继承 R2dbcRepository 和 PageableRepository）
 */
public abstract class BaseService<T, R extends R2dbcRepository<T, Long> & PageableRepository<T>> {

    protected final R repository;
    protected final String entityName;

    protected BaseService(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    // ========== 分页 ==========

    public Mono<PageResult<T>> page(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.max(size, 1);
        long offset = (long) (p - 1) * s;

        Mono<Long> countMono = repository.count().defaultIfEmpty(0L);
        Mono<java.util.List<T>> recordsMono = repository.findPage(s, offset).collectList();

        return Mono.zip(countMono, recordsMono)
                .map(tuple -> PageResult.of(tuple.getT2(), tuple.getT1(), p, s));
    }

    // ========== 查询 ==========

    public Mono<T> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(ResultCode.NOT_FOUND, entityName + "不存在: " + id)));
    }

    // ========== 新增 ==========

    public Mono<T> create(T entity) {
        prepareCreate(entity);
        return repository.save(entity);
    }

    // ========== 更新 ==========

    public Mono<T> update(Long id, T incoming) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(ResultCode.NOT_FOUND, entityName + "不存在: " + id)))
                .flatMap(existing -> {
                    mergeEntity(existing, incoming);
                    return repository.save(existing);
                });
    }

    // ========== 删除 ==========

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

    // ========== 子类钩子 ==========

    /** 新增前回调 —— 设置默认值、加密密码等 */
    protected void prepareCreate(T entity) {
        // 子类按需覆写
    }

    /** 更新时字段合并 —— 将 incoming 的非空字段写入 existing */
    protected abstract void mergeEntity(T existing, T incoming);
}
