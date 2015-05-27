package com.leavesfly.iac.execute.scheduler.pso;

import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.util.ArrayCloneUtil;

public class PsoAlgorithm<T extends RangeValue> {

	private Particle<T>[] particleSet;
	private T[] globalBestLocation;
	private float globalBestValue;

	/**
	 * 
	 * @param particleSet
	 */
	public PsoAlgorithm(Particle<T>[] particleSet) {
		this.particleSet = particleSet;
		initBestGlobalBestLocationAndValue();
	}

	private void initBestGlobalBestLocationAndValue() {

		Particle<T> bestParticle = getBestLocation();
		globalBestLocation = ArrayCloneUtil.arrayDeepCopy(bestParticle.getBestLocation());
		globalBestValue = bestParticle.getBestTargetValue();
	}

	private Particle<T> getBestLocation() {
		Particle<T> bestParticle = null;
		float bestValue = Float.MIN_VALUE;
		for (Particle<T> particle : particleSet) {
			if (bestValue < particle.getBestTargetValue()) {
				bestParticle = particle;
				bestValue = particle.getBestTargetValue();
			}
		}
		return bestParticle;
	}

	/**
	 * 
	 * @param iterateNum
	 * @return
	 */
	public T[] findBestLocation(int iterateNum) {
		while (iterateNum > 0) {
			for (Particle<T> particle : particleSet) {
				particle.updateParticle(globalBestLocation);
			}
			setBestLocationAndValue();
			iterateNum--;
		}
		return globalBestLocation;
	}

	private void setBestLocationAndValue() {
		Particle<T> bestParticle = getBestLocation();
		globalBestValue = bestParticle.getBestTargetValue();

		T[] bestLocation = bestParticle.getBestLocation();
		for (int i = 0; i < bestLocation.length; i++) {
			globalBestLocation[i].setValue(bestLocation[i].getValue());
		}
	}

	public float getGlobalBestValue() {
		return globalBestValue;
	}
}
