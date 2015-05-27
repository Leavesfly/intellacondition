package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.evalute.Evaluator;
import com.leavesfly.iac.execute.scheduler.pso.Particle;

public class PowerVectorParticle extends Particle<PowerValue> {

	/**
	 * 
	 * @param powerRangeVector
	 */
	public PowerVectorParticle(final PowerRange[] powerRangeArray) {
		super();
		PowerValue[] location = initVectorValue(powerRangeArray);
		init(location);
	}

	private PowerValue[] initVectorValue(PowerRange[] powerRangeArray) {
		PowerValue[] location = new PowerValue[powerRangeArray.length];
		for (int i = 0; i < location.length; i++) {
			location[i] = powerRangeArray[i].genInitValue();
		}
		return location;
	}

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
