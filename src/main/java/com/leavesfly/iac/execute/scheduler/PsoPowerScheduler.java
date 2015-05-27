package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.PsoAlgorithmConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.execute.PowerScheduler;
import com.leavesfly.iac.execute.scheduler.pso.Particle;
import com.leavesfly.iac.execute.scheduler.pso.PsoAlgorithm;

public class PsoPowerScheduler implements PowerScheduler {

	private PsoAlgorithm<PowerValue> psoAlgorithm;

	public PsoPowerScheduler(int initParticleNum, boolean isImprovedParticle) {

		DataFactory dataFactory = DataFactory.getInstance();
		PowerRange[] powerRangeArray = dataFactory.getPowerRangeArray();
		PowerVectorParticle[] ParticleSet = null;

		ParticleSet = new PowerVectorParticle[initParticleNum];

		if (isImprovedParticle) {
			for (int i = 0; i < initParticleNum; i++) {
				ParticleSet[i] = new PvImprovedParticle(powerRangeArray);
			}
		} else {
			for (int i = 0; i < initParticleNum; i++) {
				ParticleSet[i] = new PowerVectorParticle(powerRangeArray);
			}
		}

		psoAlgorithm = new PsoAlgorithm<PowerValue>(ParticleSet);
	}

	@Override
	public PowerVector schedule() {
		PowerValue[] bestLocation = psoAlgorithm
				.findBestLocation(PsoAlgorithmConstant.PSO_ITERATE_NUM);
		return new PowerVector(bestLocation);
	}

	public static void main(String[] args) {

		final int initParticleNum = 10;
		PowerRange[] powerRangeArray = new PowerRange[2];
		powerRangeArray[0] = new PowerRange(1.0f, 10.0f);
		powerRangeArray[1] = new PowerRange(1.0f, 10.0f);

		Particle<RangeValue>[] ParticleSet = new EvaParticle[initParticleNum];

		for (int i = 0; i < initParticleNum; i++) {
			ParticleSet[i] = new EvaParticle(powerRangeArray);
		}

		PsoAlgorithm<RangeValue> psoAlgorithm = new PsoAlgorithm<RangeValue>(ParticleSet);
		RangeValue[] location = psoAlgorithm.findBestLocation(100);

		System.out.println("---bestValue:" + location[0].getValue() + "\t" + location[1].getValue()
				+ "\t" + psoAlgorithm.getGlobalBestValue());

	}
}
