package com.leavesfly.iac.config;

/**
 * BP神经网络算法配置类
 * 
 * 提供BP神经网络相关的配置参数，采用不可变设计确保线程安全
 */
public final class BpConfig {

    private final double learningRate;
    private final int maxIterations;
    private final double errorThreshold;
    private final int hiddenLayerSize;
    private final double momentum;
    private final boolean useBias;

    private static final BpConfig INSTANCE = new BpConfig();

    private BpConfig() {
        this.learningRate = 0.1;
        this.maxIterations = 1000;
        this.errorThreshold = 0.001;
        this.hiddenLayerSize = 10;
        this.momentum = 0.9;
        this.useBias = true;
    }

    /**
     * 获取BP配置实例
     */
    public static BpConfig getInstance() {
        return INSTANCE;
    }

    public double getLearningRate() { return learningRate; }
    public int getMaxIterations() { return maxIterations; }
    public double getErrorThreshold() { return errorThreshold; }
    public int getHiddenLayerSize() { return hiddenLayerSize; }
    public double getMomentum() { return momentum; }
    public boolean isUseBias() { return useBias; }
}