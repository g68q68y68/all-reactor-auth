package com.reactorAuth.controller;

import com.reactorAuth.dto.Result;
import com.reactorAuth.entity.Permission;
import com.reactorAuth.security.PermissionCacheService;
import com.reactorAuth.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController extends BaseController<Permission, PermissionService> {

    @Autowired(required = false)
    private PermissionCacheService cacheService;

    public PermissionController(PermissionService permissionService) {
        super(permissionService);
    }

    /** 刷新权限缓存到 Redis（仅在 url-auth.enabled=true 时可用） */
    @PostMapping("/refresh-cache")
    public Mono<Result<Void>> refreshCache() {
        if (cacheService == null) {
            return Mono.just(Result.error(503, "URL 权限验证未开启，无需刷新缓存"));
        }
        return cacheService.refresh().thenReturn(Result.success());
    }
}
