package com.leavesfly.iac.domain;

import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.trainer.TrainModel;

/**
 * 功率-温度拟合函数类
 * 
 * 该类表示传感器节点位置的功率-温度映射关系，包含传感器ID、
 * 室外温度和训练模型，用于根据功率向量计算温度值。
 */
public class PtFitFunc {

	/**
	 * 传感器ID
	 */
	private String sensorId;
	
	/**
	 * 室外温度
	 */
	private float outsideTemp;
	
	/**
	 * 训练模型
	 */
	private TrainModel trainModel;

	/**
	 * 构造函数
	 * 
	 * @param sensorId 传感器ID
	 * @param outsideTemp 室外温度
	 * @param trainModel 训练模型
	 */
	public PtFitFunc(String sensorId, float outsideTemp, TrainModel trainModel) {
		this.sensorId = sensorId;
		this.outsideTemp = outsideTemp;
		this.trainModel = trainModel;
	}

	/**
	 * 根据功率向量计算温度值
	 * 
	 * @param powerVector 功率向量
	 * @return 计算得到的温度值
	 */
	public float calTemperature(PowerVector powerVector) {
		return trainModel.useMode(powerVector.getPowerValueFloatArray());
	}

	/**
	 * 获取传感器ID
	 * 
	 * @return 传感器ID
	 */
	public String getSensorId() {
		return sensorId;
	}

	/**
	 * 设置传感器ID
	 * 
	 * @param sensorId 传感器ID
	 */
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	/**
	 * 获取室外温度
	 * 
	 * @return 室外温度
	 */
	public float getOutsideTemp() {
		return outsideTemp;
	}

	/**
	 * 设置室外温度
	 * 
	 * @param outsideTemp 室外温度
	 */
	public void setOutsideTemp(float outsideTemp) {
		this.outsideTemp = outsideTemp;
	}

}