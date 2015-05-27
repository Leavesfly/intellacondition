package com.leavesfly.iac.train.domain;

import java.text.DecimalFormat;

import com.leavesfly.iac.domain.PowerVector;

public class IntellacTrainDataItem implements TrainDataItem<Float, Float> {

	private final static DecimalFormat DecimalFormat = new DecimalFormat(".0");

	private String sensorId;
	private PowerVector powerVector;
	private float temperature;
	private float outsideTemp;

	public IntellacTrainDataItem(String sensorId, PowerVector powerVector, float temperature,
			float outsideTemp) {
		this.sensorId = sensorId;
		this.powerVector = powerVector;
		this.temperature = temperature;
		this.outsideTemp = outsideTemp;
	}

	public float getOutsideTemp() {
		return outsideTemp;
	}

	public PowerVector getPowerVector() {
		return powerVector;
	}

	public void setPowerVector(PowerVector powerVector) {
		this.powerVector = powerVector;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public Float[] getFeature() {
		return powerVector.getPowerValueFloatArray();
	}

	@Override
	public Float getResult() {
		return temperature;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	@Override
	public String toString() {

		return sensorId + "\t" + DecimalFormat.format(temperature) + "\t"
				+ DecimalFormat.format(outsideTemp) + "\t" + powerVector.toString();
	}

}
