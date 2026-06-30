package com.reactorAuth.quartz.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Quartz Scheduler 手动配置。
 * <p>
 * 为什么不用 QuartzAutoConfiguration？
 * - 默认自动配置会用主 DataSource，但我们有双数据源（R2DBC + JDBC）
 * - 需要手动绑定 quartzDataSource 和 quartzTransactionManager
 * - 需要自定义 JobFactory 让 Job 支持 @Autowired Spring Bean
 * <p>
 * SchedulerFactoryBean 是 Quartz 的核心工厂：
 * - setDataSource → 绑定 JDBC 数据源，Quartz 通过 JDBC 读写 QRTZ_* 表
 * - setTransactionManager → Quartz 内部事务（JobStoreTX）
 * - setJobFactory → 让每个 Job 实例化时注入 Spring Bean（如 R2DBC Repository）
 * - setOverwriteExistingJobs(true) → 重启时用新的调度信息覆盖旧的（开发环境方便）
 */
@Configuration
public class QuartzSchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            @Qualifier("quartzDataSource") DataSource dataSource,
            @Qualifier("quartzTransactionManager") PlatformTransactionManager transactionManager,
            ApplicationContext applicationContext) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        // === 数据源 & 事务（JDBC，Quartz 内部使用）===
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);

        // === 调度器行为 ===
        factory.setAutoStartup(true);                    // 启动时自动加载已持久化的 Job
        factory.setWaitForJobsToCompleteOnShutdown(true); // 优雅关闭：等正在执行的 Job 完成
        factory.setOverwriteExistingJobs(true);           // 重启时更新 Job 定义（开发/测试用）

        // === Job 工厂：支持 @Autowired 注入 Spring Bean ===
        // Quartz 默认自己 new Job 实例，不归 Spring 管。这里重写 createJobInstance，
        // 让每个 Job 创建后走一遍 AutowireCapableBeanFactory，这样 Job 里
        // 就能 @Autowired R2DBC Repository 了（execute 里 block() 调用即可）
        factory.setJobFactory(new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(org.quartz.spi.TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
                return job;
            }
        });

        return factory;
    }
}
