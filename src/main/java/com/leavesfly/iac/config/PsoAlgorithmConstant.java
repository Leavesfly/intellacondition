package com.leavesfly.iac.config;

/**
 * PSO（粒子群优化）算法常量配置类
 * 
 * 该类定义了PSO算法的各种参数和配置常量，包括：
 * 1. 迭代参数
 * 2. 粒子群参数
 * 3. 惯性权重参数
 * 4. 学习因子参数
 */
public class PsoAlgorithmConstant {

	/**
	 * PSO算法迭代次数
	 */
	public static final int PSO_ITERATE_NUM = 1_000;
	
	/**
	 * PSO算法初始粒子数量
	 */
	public static final int PSO_INIT_PARTICLE_NUM = 100;

	/**
	 * PSO算法初始惯性权重
	 */
	public static final float PSO_INERTIA_WEIGHT_INIT = 0.9f;
	
	/**
	 * PSO算法结束惯性权重
	 */
	public static final float PSO_INERTIA_WEIGHT_END = 0.4f;
	
	/**
	 * PSO算法最大速度与位置比率
	 */
	public static final float PSO_MAX_SPEED_LOCATION_RATE = 0.1f;

	/**
	 * PSO算法个体学习因子
	 */
	public static final float PSO_LEARN_RATE_1 = 2.0f;
	
	/**
	 * PSO算法社会学习因子
	 */
	public static final float PSO_LEARN_RATE_2 = 2.0f;

	/**
	 * PSO算法中的常数0.5
	 */
	public static final float PSO_HALF_OF_ONE = 0.5f;
}