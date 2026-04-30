package com.ems.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatisticsScheduler {

    /**
     * 每5分钟预计算Dashboard统计数据（可以写入Redis缓存，当前直接查DB）
     */
    @Scheduled(fixedRate = 300000)
    public void preComputeStatistics() {
        log.info("统计数据预计算...");
        // 预留：后续可以将统计结果写入Redis缓存
    }
}
