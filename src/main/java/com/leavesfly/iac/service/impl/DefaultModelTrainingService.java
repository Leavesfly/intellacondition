package com.leavesfly.iac.service.impl;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.leavesfly.iac.async.AsyncExecutorManager;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.service.ModelTrainingService;

/**
 * 默认模型训练服务实现
 * 
 * 基于原有PtMultiThreadTrainer的现代化实现
 */
public class DefaultModelTrainingService implements ModelTrainingService {

    private final AsyncExecutorManager asyncManager;

    public DefaultModelTrainingService() {
        this.asyncManager = AsyncExecutorManager.getInstance();
    }

    @Override
    public Collection<PtFitFunc> trainModels(float outsideTemperature) {
        // TODO: 实现基于原有逻辑的训练功能
        // 这里是重构后的骨架，需要整合原有的训练逻辑
        System.out.println("开始训练模型，室外温度: " + outsideTemperature);
        
        // 模拟训练过程
        try {
            Thread.sleep(1000); // 模拟训练时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("模型训练完成");
        return null; // 实际实现需要返回训练结果
    }

    @Override
    public CompletableFuture<Collection<PtFitFunc>> trainModelsAsync(float outsideTemperature) {
        return asyncManager.executeCompute(() -> trainModels(outsideTemperature));
    }

    @Override
    public String[] getSupportedModelTypes() {
        return new String[]{"BPNN", "BPWEKA", "LR"};
    }

    @Override
    public String getTrainerName() {
        return "DefaultModelTrainer";
    }

    @Override
    public boolean supportsParallelTraining() {
        return true;
    }
}