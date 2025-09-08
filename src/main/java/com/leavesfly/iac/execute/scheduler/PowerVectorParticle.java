package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.evalute.Evaluator;
import com.leavesfly.iac.execute.scheduler.pso.Particle;

/**
 * 功率向量粒子类
 * 
 * 该类表示PSO算法中的一个粒子，代表一个功率向量配置。
 * 继承自Particle类，实现了特定于功率向量的初始化和目标值计算方法。
 */
public class PowerVectorParticle extends Particle<PowerValue> {

	/**
	 * 构造函数
	 * 
	 * @param powerRangeArray 功率范围数组
	 */
	public PowerVectorParticle(final PowerRange[] powerRangeArray) {
		super();
		PowerValue[] location = initVectorValue(powerRangeArray);
		init(location);
	}

	/**
	 * 初始化向量值
	 * 
	 * 根据功率范围数组初始化功率值数组
	 * 
	 * @param powerRangeArray 功率范围数组
	 * @return 初始化的功率值数组
	 */
	private PowerValue[] initVectorValue(PowerRange[] powerRangeArray) {
		PowerValue[] location = new PowerValue[powerRangeArray.length];
		for (int i = 0; i < location.length; i++) {
			location[i] = powerRangeArray[i].genInitValue();
		}
		return location;
	}

	/**
	 * 计算目标值
	 * 
	 * 根据功率值数组计算粒子的目标值（适应度值），
	 * 结合用户满意度和用电成本进行加权计算
	 * 
	 * @param powerValueArray 功率值数组
	 * @return 目标值
	 */
	@Override
	protected float calTargetValue(PowerValue[] powerValueArray) {

		float satisfaction = 0f;
		PowerVector powerVector = new PowerVector(powerValueArray);
		satisfaction += Evaluator.calTotalSatisfaction(powerVector);
		satisfaction *= AppContextConstant.SATISFY_WEIGHT;

		float powerCost = 0f;
		powerCost = Evaluator.calTotalPowerCost(powerVector);
		powerCost *= AppContextConstant.POWER_COST_WEIGHT;
		
		// 用户的满意度与用电消耗进行简单的归一化，便于计算
		return satisfaction - (powerCost / AppContextConstant.AIR_CONDITION_MAX_POWER)
				* ((float) AppContextConstant.USER_NUM / AppContextConstant.AIR_CONDITION_NUM);
	}

}