package com.reactorAuth.quartz.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Quartz 专用 JDBC 数据源配置。
 * <p>
 * 为什么需要独立的 JDBC 数据源？
 * - 业务代码使用 R2DBC（响应式，非阻塞，Netty Event Loop）
 * - Quartz 使用 JDBC（阻塞式，自有线程池，JobStoreTX）
 * - 两者共用同一个 MySQL 库（security_db），但访问方式完全不同
 * - HikariCP 连接池只需 5 个连接——Quartz 本身线程数不多
 * <p>
 * 数据源绑定到 yml 的 spring.datasource.quartz.* 配置
 */
@Configuration
public class QuartzDataSourceConfig {

    @Value("${spring.datasource.quartz.url}")
    private String url;
    @Value("${spring.datasource.quartz.username}")
    private String username;
    @Value("${spring.datasource.quartz.password}")
    private String password;
    @Value("${spring.datasource.quartz.driver-class-name}")
    private String driverClassName;

    /**
     * Quartz 专用 HikariCP 数据源。
     * Bean 命名为 quartzDataSource，与业务 DataSource 区分
     */
    @Bean(name = "quartzDataSource")
    public DataSource quartzDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        ds.setMaximumPoolSize(5); // Quartz 不需要太多连接
        return ds;
    }

    /**
     * Quartz 专用 JDBC 事务管理器。
     * 业务代码走 R2DBC 事务（ReactiveTransactionManager），
     * Quartz 走这个 JDBC 事务（DataSourceTransactionManager），两者完全隔离
     */
    @Bean(name = "quartzTransactionManager")
    public DataSourceTransactionManager quartzTransactionManager(DataSource quartzDataSource) {
        return new DataSourceTransactionManager(quartzDataSource);
    }
}
