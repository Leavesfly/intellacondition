package com.leavesfly.iac.domain;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.leavesfly.iac.config.AppContextConstant;

public class PowerVector implements Serializable {

	private final static DecimalFormat DecimalFormat = new DecimalFormat(".0");
	private static final long serialVersionUID = 1L;

	private PowerValue[] powerValueVector;
	private int size;

	public PowerVector(PowerValue[] powerValueVector) {
		this.powerValueVector = powerValueVector;
		this.size = powerValueVector.length;
	}

	public PowerVector(Float[] powerValueArray, int length) {
		if (powerValueArray == null || powerValueArray.length != length) {
			throw new IllegalArgumentException();
		}
		powerValueVector = new PowerValue[length];
		size = length;
		for (int i = 0; i < size; i++) {
			powerValueVector[i] = new PowerValue(powerValueArray[i], new PowerRange(
					AppContextConstant.AIR_CONDITION_MIN_POWER, AppContextConstant.AIR_CONDITION_MAX_POWER));
		}
	}

	public PowerValue[] getPowerValueVector() {
		return powerValueVector;
	}

	public void setPowerValueVector(PowerValue[] powerValueVector) {
		this.powerValueVector = powerValueVector;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Float[] getPowerValueFloatArray() {
		Float[] value = new Float[powerValueVector.length];
		for (int i = 0; i < value.length; i++) {
			value[i] = powerValueVector[i].getValue();
		}
		return value;
	}

	public static PowerVector getInstanceBySameRange(float from, float to, int size) {
		PowerValue[] powerValueVector = new PowerValue[size];
		for (int i = 0; i < size; i++) {
			powerValueVector[i] = new PowerValue(new PowerRange(from, to));
		}
		return new PowerVector(powerValueVector);
	}

	@Override
	public String toString() {
		StringBuilder strbuilder = new StringBuilder("[");
		for (int i = 0; i < powerValueVector.length; i++) {
			strbuilder.append(DecimalFormat.format(powerValueVector[i].getValue())).append(",");
		}
		if (powerValueVector.length > 0) {
			strbuilder.deleteCharAt(strbuilder.length() - 1);
		}
		strbuilder.append("]");
		return strbuilder.toString();
	}
}
