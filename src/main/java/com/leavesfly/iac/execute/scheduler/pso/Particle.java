package com.leavesfly.iac.execute.scheduler.pso;

import com.leavesfly.iac.config.PsoAlgorithmConstant;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.util.ArrayCloneUtil;

/**
 * 粒子群算法的抽象粒子
 * 
 * @author LeavesFly
 *
 * @param <T>
 */
public abstract class Particle<T extends RangeValue> {

	protected static final float INERTIA_WEIGHT_INIT = PsoAlgorithmConstant.PSO_INERTIA_WEIGHT_INIT;
	private static final float INERTIA_WEIGHT_END = PsoAlgorithmConstant.PSO_INERTIA_WEIGHT_END;
	private static final float MAX_SPEED_LOCATION_RATE = PsoAlgorithmConstant.PSO_MAX_SPEED_LOCATION_RATE;

	protected static final float LEARN_RATE_1 = PsoAlgorithmConstant.PSO_LEARN_RATE_1;
	protected static final float LEARN_RATE_2 = PsoAlgorithmConstant.PSO_LEARN_RATE_2;

	protected static final float HALF_OF_ONE = PsoAlgorithmConstant.PSO_HALF_OF_ONE;
	public final static int iterateNum = PsoAlgorithmConstant.PSO_ITERATE_NUM;
	public final static float inertiaWeightFrequency = (INERTIA_WEIGHT_INIT - INERTIA_WEIGHT_END)
			/ iterateNum;

	protected T[] location;
	protected T[] speed;
	private float currentTargetValue;

	protected T[] bestLocation;
	private float bestTargetValue;

	protected int currentIterateNum = 0;

	protected Particle() {
	}

	protected void init(T[] location) {
		this.location = location;
		initSpeed();
		currentTargetValue = calTargetValue();

		bestLocation = ArrayCloneUtil.arrayDeepCopy(location);
		bestTargetValue = currentTargetValue;
	}

	private void initSpeed() {
		speed = ArrayCloneUtil.arrayDeepCopy(location);
		for (int i = 0; i < location.length; i++) {
			float maxSpeed = (location[i].getTo() - location[i].getFrom())
					* MAX_SPEED_LOCATION_RATE;
			speed[i].setValue((2 * (float) Math.random() - 1) * maxSpeed);
			speed[i].setFrom(-maxSpeed);
			speed[i].setTo(maxSpeed);
		}
	}

	protected abstract float calTargetValue(T[] location);

	/**
	 * 
	 * @param globalBestLocation
	 */
	public void updateParticle(T[] globalBestLocation) {

		genNewSpeedAndLocation(globalBestLocation);
		currentTargetValue = calTargetValue(location);

		if (currentTargetValue > bestTargetValue) {
			changeBestTargetAndLocation();
		}

		currentIterateNum++;
	}

	private float calTargetValue() {
		return calTargetValue(location);
	}

	private void genNewSpeedAndLocation(T[] globalBestLocation) {
		genNewSpeed(globalBestLocation);
		genNewLocation();
	}

	protected void genNewSpeed(T[] globalBestLocation) {
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

	protected void genNewLocation() {
		for (int i = 0; i < location.length; i++) {
			float value = location[i].getValue() + speed[i].getValue();
			if (value > location[i].getTo()) {
				location[i].setValue(location[i].getTo());
			} else if (value < location[i].getFrom()) {
				location[i].setValue(location[i].getFrom());
			} else {
				location[i].setValue(value);
			}
		}
	}

	private void changeBestTargetAndLocation() {
		bestTargetValue = currentTargetValue;
		for (int i = 0; i < bestLocation.length; i++) {
			bestLocation[i].setValue(location[i].getValue());
		}
	}

	/**
	 * 
	 * @return
	 */
	public T[] getBestLocation() {
		return bestLocation;
	}

	/**
	 * 
	 * @return
	 */
	public float getBestTargetValue() {
		return bestTargetValue;
	}

}
