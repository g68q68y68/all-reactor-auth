package com.reactorAuth.controller;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.AuditLog;
import com.reactorAuth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogRepository repository;

    @GetMapping
    public Mono<Result<PageResult<AuditLog>>> page(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        int p = Math.max(page, 1), s = Math.max(size, 1);
        long offset = (long) (p - 1) * s;
        return repository.count().defaultIfEmpty(0L)
                .zipWith(repository.findPage(s, offset).collectList())
                .map(t -> PageResult.of(t.getT2(), t.getT1(), p, s))
                .map(Result::success);
    }
}
