package com.leavesfly.iac.service;

import java.util.concurrent.CompletableFuture;

import com.leavesfly.iac.domain.improved.PowerVector;

/**
 * 空调功率调度服务接口
 * 
 * 定义空调功率调度的核心业务接口，支持同步和异步调度
 */
public interface PowerSchedulingService {

    /**
     * 同步执行功率调度
     * 
     * @return 优化后的功率向量
     */
    PowerVector schedule();

    /**
     * 异步执行功率调度
     * 
     * @return 异步任务Future
     */
    CompletableFuture<PowerVector> scheduleAsync();

    /**
     * 获取调度器名称
     */
    String getSchedulerName();

    /**
     * 是否支持并行处理
     */
    boolean supportsParallelProcessing();
}