package com.reactorAuth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解。标记在 Controller 方法上，AOP 自动记录操作日志。
 * <p>
 * 示例：@AuditLog(module = "用户管理", action = "新增用户", description = "创建了新账号")
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    String module();        // 操作模块
    String action();        // 操作类型
    String description() default ""; // 详情（支持 SpEL）
}
