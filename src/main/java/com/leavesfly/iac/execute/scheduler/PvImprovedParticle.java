package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.util.MathUtil;

/**
 * 混沌粒子
 * 
 * @author LeavesFly
 *
 */
public class PvImprovedParticle extends PowerVectorParticle {

	private final static float[] rmChaoticVariable;

	static {
		rmChaoticVariable = new float[AppContextConstant.AIR_CONDITION_NUM];
		for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
			rmChaoticVariable[i] = MathUtil.nextInt(10, 20);
		}
	}

	private float[] mchaoticVariable;
	private final float[] umChaoticVariable;
	private final float sChaoticVariable;

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
