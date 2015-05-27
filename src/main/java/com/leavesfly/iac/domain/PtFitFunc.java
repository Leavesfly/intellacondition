package com.leavesfly.iac.domain;

import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.trainer.TrainModel;

/**
 * 节点位置的功率-温度拟合函数
 * 
 * @author yefei.yf
 *
 */
public class PtFitFunc {

	private String sensorId;
	private float outsideTemp;
	private TrainModel trainModel;

	public PtFitFunc(String sensorId, float outsideTemp, TrainModel trainModel) {
		this.sensorId = sensorId;
		this.outsideTemp = outsideTemp;
		this.trainModel = trainModel;
	}

	public float calTemperature(PowerVector powerVector) {
		return trainModel.useMode(powerVector.getPowerValueFloatArray());
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public float getOutsideTemp() {
		return outsideTemp;
	}

	public void setOutsideTemp(float outsideTemp) {
		this.outsideTemp = outsideTemp;
	}

}
