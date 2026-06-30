package com.reactorAuth.controller;

import com.reactorAuth.service.ControllableSinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
@Slf4j
public class StreamController {

    private final ControllableSinkService streamService;

    /**
     * 启动数据流
     * POST /api/stream/start
     * 
     * 执行流程：
     * 1. zip 等待 ABC 初始化完成
     * 2. 启动消费者 D
     * 3. ABC 开始持续生产数据
     * 4. D 持续消费
     */
    @PostMapping("/start")
    public Mono<Map<String, String>> start() {
        log.info("📨 收到启动请求");
        
        return streamService.start()
                .thenReturn(Map.of(
                        "status", "success",
                        "message", "数据流已启动，ABC正在生产，D正在消费"
                ))
                .onErrorResume(e -> {
                    log.error("启动失败", e);
                    return Mono.just(Map.of(
                            "status", "error",
                            "message", "启动失败: " + e.getMessage()
                    ));
                });
    }

    /**
     * 停止数据流
     * POST /api/stream/stop
     */
    @PostMapping("/stop")
    public Mono<Map<String, Object>> stop() {
        log.info("📨 收到停止请求");
        
        return streamService.stop()
                .thenReturn(Map.of(
                        "status", "success",
                        "message", "数据流已停止",
                        "stats", streamService.getStatus()
                ));
    }

    /**
     * 获取状态
     * GET /api/stream/status
     */
    @GetMapping("/status")
    public Mono<Map<String, Object>> getStatus() {
        return Mono.just(streamService.getStatus());
    }
}