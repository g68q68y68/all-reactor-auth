package com.reactorAuth.controller;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.service.BaseService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 单表通用 Controller 基类 —— 提供分页、增删改查 REST 端点
 *
 * @param <T> 实体类型
 * @param <S> Service 类型
 */
public abstract class BaseController<T, S extends BaseService<T, ?>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public Mono<Result<PageResult<T>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.page(page, size).map(Result::success);
    }

    @GetMapping("/{id}")
    public Mono<Result<T>> findById(@PathVariable Long id) {
        return service.findById(id).map(Result::success);
    }

    @PostMapping
    public Mono<Result<T>> create(@RequestBody T entity) {
        return service.create(entity).map(Result::success);
    }

    @PutMapping("/{id}")
    public Mono<Result<T>> update(@PathVariable Long id, @RequestBody T entity) {
        return service.update(id, entity).map(Result::success);
    }

    @DeleteMapping("/{id}")
    public Mono<Result<Void>> delete(@PathVariable Long id) {
        return service.delete(id).thenReturn(Result.success());
    }
}
