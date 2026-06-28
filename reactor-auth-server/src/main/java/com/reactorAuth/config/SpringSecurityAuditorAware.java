package com.reactorAuth.config;

import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 从 SecurityContext 获取当前登录用户 ID，供 @CreatedBy / @LastModifiedBy 自动填充。
 * 对标 MyBatis-Plus MetaObjectHandler 的 insertFill/updateFill 中获取当前用户。
 */
@Component
public class SpringSecurityAuditorAware implements ReactiveAuditorAware<Long> {

    @Override
    public Mono<Long> getCurrentAuditor() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof String) {
                        try {
                            return Long.valueOf((String) principal);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return null;
                });
    }
}
