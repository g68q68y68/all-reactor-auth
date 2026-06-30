package com.reactorAuth.quartz.service;

import com.reactorAuth.dto.PageResult;
import com.reactorAuth.quartz.entity.QuartzJob;
import com.reactorAuth.quartz.repository.QuartzJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzJobService {

    private final QuartzJobRepository repository;
    private final Scheduler scheduler;

    public Mono<PageResult<QuartzJob>> page(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.max(size, 1);
        long offset = (long) (p - 1) * s;
        return repository.count().defaultIfEmpty(0L)
                .zipWith(repository.findPage(s, offset).collectList())
                .map(t -> PageResult.of(t.getT2(), t.getT1(), p, s));
    }

    public Mono<QuartzJob> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<QuartzJob> create(QuartzJob job) {
        job.setId(null);
        job.setStatus(1);
        return repository.save(job)
                .flatMap(saved -> scheduleJob(saved).thenReturn(saved));
    }

    public Mono<QuartzJob> update(Long id, QuartzJob incoming) {
        return repository.findById(id)
                .flatMap(existing -> {
                    if (incoming.getJobName() != null) existing.setJobName(incoming.getJobName());
                    if (incoming.getJobGroup() != null) existing.setJobGroup(incoming.getJobGroup());
                    if (incoming.getCronExpression() != null) existing.setCronExpression(incoming.getCronExpression());
                    if (incoming.getJobClass() != null) existing.setJobClass(incoming.getJobClass());
                    if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
                    if (incoming.getParams() != null) existing.setParams(incoming.getParams());
                    return repository.save(existing)
                            .flatMap(saved -> rescheduleJob(saved).thenReturn(saved));
                });
    }

    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .flatMap(job -> unscheduleJob(job).then(repository.deleteById(id)));
    }

    public Mono<Void> pause(Long id) {
        return repository.findById(id)
                .flatMap(job -> {
                    job.setStatus(0);
                    return repository.save(job).then(pauseJob(job));
                }).then();
    }

    public Mono<Void> resume(Long id) {
        return repository.findById(id)
                .flatMap(job -> {
                    job.setStatus(1);
                    return repository.save(job).then(resumeJob(job));
                }).then();
    }

    public Mono<Void> trigger(Long id) {
        return repository.findById(id)
                .flatMap(job -> Mono.fromRunnable(() -> {
                    try {
                        JobKey key = JobKey.jobKey(job.getJobName(), job.getJobGroup());
                        scheduler.triggerJob(key);
                        log.info("手动触发任务: {}", key);
                    } catch (SchedulerException e) {
                        log.error("触发任务失败: {}", e.getMessage());
                    }
                }).subscribeOn(Schedulers.boundedElastic())).then();
    }

    /** 注册调度 */
    private Mono<Void> scheduleJob(QuartzJob job) {
        return Mono.fromRunnable(() -> {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(job.getJobClass());
                JobDetail detail = JobBuilder.newJob(clazz)
                        .withIdentity(job.getJobName(), job.getJobGroup())
                        .usingJobData("params", job.getParams() != null ? job.getParams() : "")
                        .build();
                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(job.getJobName() + "_trigger", job.getJobGroup())
                        .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                        .build();
                scheduler.scheduleJob(detail, trigger);
                log.info("任务已调度: {}", job.getJobName());
            } catch (Exception e) {
                log.error("调度任务失败: {}", e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private Mono<Void> rescheduleJob(QuartzJob job) {
        return unscheduleJob(job).then(scheduleJob(job));
    }

    private Mono<Void> unscheduleJob(QuartzJob job) {
        return Mono.fromRunnable(() -> {
            try {
                TriggerKey tKey = TriggerKey.triggerKey(job.getJobName() + "_trigger", job.getJobGroup());
                scheduler.unscheduleJob(tKey);
                scheduler.deleteJob(JobKey.jobKey(job.getJobName(), job.getJobGroup()));
                log.info("任务已移除: {}", job.getJobName());
            } catch (Exception e) {
                log.warn("移除任务失败: {}", e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private Mono<Void> pauseJob(QuartzJob job) {
        return Mono.fromRunnable(() -> {
            try { scheduler.pauseJob(JobKey.jobKey(job.getJobName(), job.getJobGroup())); }
            catch (Exception e) { log.warn("暂停失败: {}", e.getMessage()); }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private Mono<Void> resumeJob(QuartzJob job) {
        return Mono.fromRunnable(() -> {
            try { scheduler.resumeJob(JobKey.jobKey(job.getJobName(), job.getJobGroup())); }
            catch (Exception e) { log.warn("恢复失败: {}", e.getMessage()); }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
