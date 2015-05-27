package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.execute.scheduler.pso.Particle;

public class EvaParticle extends Particle<RangeValue> {

	public EvaParticle(final PowerRange[] powerRangeArray) {
		super();
		PowerValue[] location = new PowerValue[powerRangeArray.length];
		for (int i = 0; i < location.length; i++) {
			location[i] = powerRangeArray[i].genInitValue();
		}
		init(location);
	}

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
