package com.reactorAuth.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        String params = context.getMergedJobDataMap().getString("params");
        log.info("DemoJob 执行中... 参数: {}", params);
    }
}
