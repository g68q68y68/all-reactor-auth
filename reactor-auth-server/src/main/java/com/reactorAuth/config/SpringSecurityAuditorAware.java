package com.reactorAuth.config;

import com.reactorAuth.utils.LoginUserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 从 SecurityContext 获取当前登录用户 ID，供 @CreatedBy / @LastModifiedBy 自动填充。
 * 对标 MyBatis-Plus MetaObjectHandler 的 insertFill/updateFill 中获取当前用户。
 */
@Component
@AllArgsConstructor
@Slf4j
public class SpringSecurityAuditorAware implements ReactiveAuditorAware<Long> {

    private final LoginUserUtil loginUserUtil;

    @Override
    public Mono<Long> getCurrentAuditor() {
        return loginUserUtil.getUserId()
                .doOnError(e -> log.error("获取当前审计用户ID失败: {}", e.getMessage()))
                .onErrorResume(e -> {
                    // 记录错误日志
                    log.error("获取当前审计用户ID异常: {}", e.getMessage(), e);
                    // 返回默认值（1L 表示系统用户）
                    return Mono.just(1L);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("当前用户未登录，使用默认审计用户ID: 1L");
                    return Mono.just(1L);
                }));
    }
}
