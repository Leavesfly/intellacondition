package com.leavesfly.iac.service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.leavesfly.iac.domain.PtFitFunc;

/**
 * 模型训练服务接口
 * 
 * 定义模型训练的核心业务接口，支持多种训练算法和异步训练
 */
public interface ModelTrainingService {

    /**
     * 同步训练模型
     * 
     * @param outsideTemperature 室外温度
     * @return 训练完成的功率-温度拟合函数集合
     */
    Collection<PtFitFunc> trainModels(float outsideTemperature);

    /**
     * 异步训练模型
     * 
     * @param outsideTemperature 室外温度
     * @return 异步任务Future
     */
    CompletableFuture<Collection<PtFitFunc>> trainModelsAsync(float outsideTemperature);

    /**
     * 获取支持的模型类型
     */
    String[] getSupportedModelTypes();

    /**
     * 获取训练器名称
     */
    String getTrainerName();

    /**
     * 是否支持并行训练
     */
    boolean supportsParallelTraining();
}