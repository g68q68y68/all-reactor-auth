package com.reactorAuth.quartz.controller;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.dto.Result;
import com.reactorAuth.quartz.entity.QuartzJob;
import com.reactorAuth.quartz.service.QuartzJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/quartz-jobs")
@RequiredArgsConstructor
public class QuartzJobController {
    private final QuartzJobService service;

    @GetMapping
    public Mono<Result<PageResult<QuartzJob>>> page(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return service.page(page, size).map(Result::success);
    }

    @GetMapping("/{id}")
    public Mono<Result<QuartzJob>> findById(@PathVariable Long id) {
        return service.findById(id).map(Result::success);
    }

    @PostMapping
    public Mono<Result<QuartzJob>> create(@RequestBody QuartzJob job) {
        return service.create(job).map(Result::success);
    }

    @PutMapping("/{id}")
    public Mono<Result<QuartzJob>> update(@PathVariable Long id, @RequestBody QuartzJob job) {
        return service.update(id, job).map(Result::success);
    }

    @DeleteMapping("/{id}")
    public Mono<Result<Void>> delete(@PathVariable Long id) {
        return service.delete(id).thenReturn(Result.success());
    }

    @PostMapping("/{id}/pause")
    public Mono<Result<Void>> pause(@PathVariable Long id) {
        return service.pause(id).thenReturn(Result.success());
    }

    @PostMapping("/{id}/resume")
    public Mono<Result<Void>> resume(@PathVariable Long id) {
        return service.resume(id).thenReturn(Result.success());
    }

    @PostMapping("/{id}/trigger")
    public Mono<Result<Void>> trigger(@PathVariable Long id) {
        return service.trigger(id).thenReturn(Result.success());
    }
}
