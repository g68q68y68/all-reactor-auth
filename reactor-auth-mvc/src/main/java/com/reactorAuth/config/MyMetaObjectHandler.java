package com.reactorAuth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        Long userId = getCurrentUserId();
        if (userId != null) {
            this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
            this.strictInsertFill(metaObject, "updatedBy", Long.class, userId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        Long userId = getCurrentUserId();
        if (userId != null) {
            this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
        }
    }

    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof String) {
                return Long.valueOf((String) auth.getPrincipal());
            }
        } catch (Exception ignored) {}
        return null;
    }
}
