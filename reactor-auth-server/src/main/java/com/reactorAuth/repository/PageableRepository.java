package com.reactorAuth.repository;

import reactor.core.publisher.Flux;

/**
 * 分页查询能力 —— 单表 Repository 实现此接口即可接入 BaseService 分页
 */
public interface PageableRepository<T> {

    /**
     * 分页查询
     * @param limit  每页条数
     * @param offset 偏移量
     */
    Flux<T> findPage(int limit, long offset);
}
