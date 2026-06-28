package com.reactorAuth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * 开启 R2DBC 审计，自动填充 @CreatedDate / @LastModifiedDate。
 * 对标 MyBatis-Plus MetaObjectHandler，但无需手写反射——Spring Data 原生支持。
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcAuditingConfig {
}
