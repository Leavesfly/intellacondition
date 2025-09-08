package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.util.MathUtil;

/**
 * 改进的功率向量粒子类（混沌粒子）
 * 
 * 该类表示一种改进的PSO粒子，引入了混沌变量来增强算法的全局搜索能力，
 * 避免陷入局部最优解。
 */
public class PvImprovedParticle extends PowerVectorParticle {

	/**
	 * 混沌变量数组
	 */
	private final static float[] rmChaoticVariable;

	static {
		rmChaoticVariable = new float[AppContextConstant.AIR_CONDITION_NUM];
		for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
			rmChaoticVariable[i] = MathUtil.nextInt(10, 20);
		}
	}

	/**
	 * 混沌变量m
	 */
	private float[] mchaoticVariable;
	
	/**
	 * 混沌变量um
	 */
	private final float[] umChaoticVariable;
	
	/**
	 * 混沌变量s
	 */
	private final float sChaoticVariable;

	/**
	 * 构造函数
	 * 
	 * @param powerRangeVector 功率范围数组
	 */
	public PvImprovedParticle(PowerRange[] powerRangeVector) {
		super(powerRangeVector);

		mchaoticVariable = new float[powerRangeVector.length];
		umChaoticVariable = new float[powerRangeVector.length];
		for (int i = 0; i < powerRangeVector.length; i++) {
			mchaoticVariable[i] = MathUtil.nextFloat(0.0f, 1.0f);
			umChaoticVariable[i] = MathUtil.nextFloat(0.0f, 1.0f);
		}
		sChaoticVariable = 0.3f;
	}

	/**
	 * 生成新速度
	 * 
	 * 根据全局最优位置和混沌变量计算新的粒子速度
	 * 
	 * @param globalBestLocation 全局最优位置
	 */
	@Override
	protected void genNewSpeed(PowerValue[] globalBestLocation) {
		for (int i = 0; i < globalBestLocation.length; i++) {
			float inertiaWeight = INERTIA_WEIGHT_INIT - inertiaWeightFrequency * currentIterateNum;

			float value = inertiaWeight * speed[i].getValue() + LEARN_RATE_1
					* (float) Math.random() * (bestLocation[i].getValue() - location[i].getValue())
					+ LEARN_RATE_2 * (float) Math.random()
					* (globalBestLocation[i].getValue() - location[i].getValue());

			if (speed[i].isInRange(value)) {
				speed[i].setValue(value);
			} else {
				speed[i].setValue(Math.random() >= HALF_OF_ONE ? speed[i].getFrom() : speed[i]
						.getTo());
			}
		}
	}

	/**
	 * 生成新位置
	 * 
	 * 根据混沌变量和当前状态计算新的粒子位置
	 */
	@Override
	protected void genNewLocation() {

		for (int i = 0; i < mchaoticVariable.length; i++) {
			mchaoticVariable[i] = (float) Math.pow(mchaoticVariable[i], umChaoticVariable[i] + 1);
		}

		for (int i = 0; i < location.length; i++) {
			float tmp = rmChaoticVariable[i] * sChaoticVariable;
			float value = (float) ((location[i].getValue() + tmp)
					* Math.exp((1 - Math.exp(-10.f * mchaoticVariable[i]))
							* (3.0f - 7.5f / rmChaoticVariable[i] * (location[i].getValue() + tmp)))
					- tmp + Math.exp(-20.0f * mchaoticVariable[i] * speed[i].getValue()));

			if (value > location[i].getTo()) {
				location[i].setValue(location[i].getTo());
			} else if (value < location[i].getFrom()) {
				location[i].setValue(location[i].getFrom());
			} else {
				location[i].setValue(value);
			}
		}
	}
}