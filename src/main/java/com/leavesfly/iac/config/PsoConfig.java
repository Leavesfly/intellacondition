package com.leavesfly.iac.config;

/**
 * PSO算法配置类
 * 
 * 提供PSO算法相关的配置参数，采用不可变设计确保线程安全
 */
public final class PsoConfig {

    private final int initParticleNum;
    private final int iterateNum;
    private final float w;
    private final float c1;
    private final float c2;
    private final float maxVelocity;
    private final float minVelocity;

    private static final PsoConfig INSTANCE = new PsoConfig();

    private PsoConfig() {
        this.initParticleNum = 30;
        this.iterateNum = 300;
        this.w = 0.9f;
        this.c1 = 2.0f;
        this.c2 = 2.0f;
        this.maxVelocity = 50.0f;
        this.minVelocity = -50.0f;
    }

    /**
     * 获取PSO配置实例
     */
    public static PsoConfig getInstance() {
        return INSTANCE;
    }

    public int getInitParticleNum() { return initParticleNum; }
    public int getIterateNum() { return iterateNum; }
    public float getW() { return w; }
    public float getC1() { return c1; }
    public float getC2() { return c2; }
    public float getMaxVelocity() { return maxVelocity; }
    public float getMinVelocity() { return minVelocity; }
}