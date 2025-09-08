package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.execute.scheduler.pso.Particle;

/**
 * 评估粒子类
 * 
 * 该类是一个用于测试和评估的粒子实现，使用简单的平方和函数作为目标函数。
 */
public class EvaParticle extends Particle<RangeValue> {

	/**
	 * 构造函数
	 * 
	 * @param powerRangeArray 功率范围数组
	 */
	public EvaParticle(final PowerRange[] powerRangeArray) {
		super();
		PowerValue[] location = new PowerValue[powerRangeArray.length];
		for (int i = 0; i < location.length; i++) {
			location[i] = powerRangeArray[i].genInitValue();
		}
		init(location);
	}

	/**
	 * 计算目标值
	 * 
	 * 使用平方和函数计算目标值：f(x) = x1^2 + x2^2 + ... + xn^2
	 * 
	 * @param location 位置数组
	 * @return 目标值
	 */
	@Override
	protected float calTargetValue(RangeValue[] location) {
		float satisfaction = 0f;
		for (int i = 0; i < location.length; i++) {
			satisfaction += (float) Math.pow(location[i].getValue(), 2.0f);
		}
		// satisfaction += 1.0f / location[0].getValue() + 1.0f
		// / Math.pow(location[1].getValue(), 2.0f);
		// System.out.println(location[0].getValue() + "\t" +
		// location[1].getValue() + "\t"
		// + satisfaction);
		return satisfaction;
	}

}