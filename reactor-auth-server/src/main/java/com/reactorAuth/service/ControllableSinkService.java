package com.reactorAuth.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.Scannable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ControllableSinkService {

    private final Sinks.Many<String> sink = Sinks.many()
            .multicast()
            .onBackpressureBuffer(10000);

    // ✅ 保存所有 Disposable 引用
    private volatile Disposable mainSubscription;
    private volatile Disposable consumerSubscription;
    private volatile Disposable producerASubscription;
    private volatile Disposable producerBSubscription;
    private volatile Disposable producerCSubscription;
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicLong totalProduced = new AtomicLong(0);
    private final AtomicLong totalConsumed = new AtomicLong(0);

    /**
     * 启动服务
     */
    public Mono<Void> start() {
        if (isRunning.get()) {
            log.warn("服务已在运行中");
            return Mono.empty();
        }

        log.info("🚀 启动服务，等待所有生产者初始化...");

        // ✅ 1. zip 等待所有生产者初始化完成
        return Mono.zip(
                initializeProducerA(),
                initializeProducerB(),
                initializeProducerC()
        )
        .doOnSubscribe(s -> log.info("⏳ 正在初始化生产者 A、B、C..."))
        .doOnSuccess(tuple -> {
            log.info("✅ 所有生产者初始化完成！");
            log.info("   生产者A: {}", tuple.getT1());
            log.info("   生产者B: {}", tuple.getT2());
            log.info("   生产者C: {}", tuple.getT3());
            
            // 2. 启动所有组件
            startAllComponents();
            
            isRunning.set(true);
            log.info("🎯 系统已启动，开始持续生产消费...");
        })
        .doOnError(e -> log.error("❌ 启动失败", e))
        .then();
    }

    /**
     * 启动所有组件（消费者 + 生产者）
     */
    private void startAllComponents() {
        // ✅ 2.1 启动消费者D（保存 subscription）
        startConsumer();
        
        // ✅ 2.2 启动三个生产者（保存 subscriptions）
        startProducers();
    }

    /**
     * 启动消费者D
     */
    private void startConsumer() {
        log.info("【消费者D】启动，开始监听数据...");
        
        // ✅ 保存消费者订阅
        consumerSubscription = sink.asFlux()
                .doOnSubscribe(s -> log.info("【消费者D】已订阅"))
                .doOnNext(data -> {
                    consumeData(data);
                    totalConsumed.incrementAndGet();
                })
                .doOnError(e -> log.error("【消费者D】消费出错", e))
                .doOnComplete(() -> log.info("【消费者D】停止消费"))
                .subscribeOn(Schedulers.boundedElastic())  // 使用弹性线程池
                .subscribe();  // ✅ 返回 Disposable
    }

    /**
     * 启动三个生产者
     */
    private void startProducers() {
        // ✅ 生产者A：保存 subscription
        producerASubscription = Mono.just("A")
                .repeat()
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s -> log.info("【生产者A】开始生产..."))
                .subscribeOn(Schedulers.parallel())
                .subscribe(i -> {
                    String data = "A-" + System.currentTimeMillis() + "-" + totalProduced.incrementAndGet();
                    emitData(data);
                });

        // ✅ 生产者B：保存 subscription
        producerBSubscription = Mono.just("B")
                .repeat()
                .delayElements(Duration.ofMillis(200))
                .doOnSubscribe(s -> log.info("【生产者B】开始生产..."))
                .subscribeOn(Schedulers.parallel())
                .subscribe(i -> {
                    String data = "B-" + System.currentTimeMillis() + "-" + totalProduced.incrementAndGet();
                    emitData(data);
                });

        // ✅ 生产者C：保存 subscription
        producerCSubscription = Mono.just("C")
                .repeat()
                .delayElements(Duration.ofMillis(500))
                .doOnSubscribe(s -> log.info("【生产者C】开始生产..."))
                .subscribeOn(Schedulers.parallel())
                .subscribe(i -> {
                    String data = "C-" + System.currentTimeMillis() + "-" + totalProduced.incrementAndGet();
                    emitData(data);
                });
    }

    private Mono<String> initializeProducerA() {
        return Mono.just("ProducerA")
                .delayElement(Duration.ofMillis(500))
                .doOnNext(name -> log.info("【生产者A】初始化完成"))
                .map(name -> name + "-Ready");
    }

    private Mono<String> initializeProducerB() {
        return Mono.just("ProducerB")
                .delayElement(Duration.ofMillis(800))
                .doOnNext(name -> log.info("【生产者B】初始化完成"))
                .map(name -> name + "-Ready");
    }

    private Mono<String> initializeProducerC() {
        return Mono.just("ProducerC")
                .delayElement(Duration.ofMillis(300))
                .doOnNext(name -> log.info("【生产者C】初始化完成"))
                .map(name -> name + "-Ready");
    }

    private void consumeData(String data) {
        log.debug("【消费者D】消费: {}", data);
        // 业务处理逻辑
    }

    private void emitData(String data) {
        Sinks.EmitResult result = sink.tryEmitNext(data);
        if (result.isFailure()) {
            log.warn("发送失败: {}", result);
            sink.emitNext(data, (signalType, emitResult) -> {
                log.warn("重试发送: {}", emitResult);
                return true;
            });
        }
    }

    /**
     * ✅ 停止服务 - 取消所有订阅
     */
    public Mono<Void> stop() {
        if (!isRunning.get()) {
            log.warn("服务未运行");
            return Mono.empty();
        }

        log.info("🛑 停止服务...");
        
        // ✅ 取消所有订阅
        disposeQuietly(mainSubscription);
        disposeQuietly(consumerSubscription);
        disposeQuietly(producerASubscription);
        disposeQuietly(producerBSubscription);
        disposeQuietly(producerCSubscription);
        
        // 关闭 Sink
        sink.emitComplete((signalType, emitResult) -> {
            log.info("Sink 已关闭");
            return false;
        });
        
        isRunning.set(false);
        log.info("✅ 服务已停止。统计: 生产={}, 消费={}", 
                totalProduced.get(), totalConsumed.get());
        
        return Mono.empty();
    }

    /**
     * 安全取消订阅
     */
    private void disposeQuietly(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            log.debug("已取消订阅: {}", disposable);
        }
    }

    /**
     * 获取运行状态
     */
    public Map<String, Object> getStatus() {
        return Map.of(
                "isRunning", isRunning.get(),
                "totalProduced", totalProduced.get(),
                "totalConsumed", totalConsumed.get(),
                "queueSize", sink.scan(Scannable.Attr.BUFFERED),
                "subscriptions", Map.of(
                        "main", mainSubscription != null && !mainSubscription.isDisposed(),
                        "consumer", consumerSubscription != null && !consumerSubscription.isDisposed(),
                        "producerA", producerASubscription != null && !producerASubscription.isDisposed(),
                        "producerB", producerBSubscription != null && !producerBSubscription.isDisposed(),
                        "producerC", producerCSubscription != null && !producerCSubscription.isDisposed()
                )
        );
    }

    @PreDestroy
    public void cleanup() {
        stop().subscribe();
    }
}