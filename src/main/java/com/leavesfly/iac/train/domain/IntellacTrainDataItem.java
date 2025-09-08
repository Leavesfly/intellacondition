package com.leavesfly.iac.train.domain;

import java.text.DecimalFormat;

import com.leavesfly.iac.domain.PowerVector;

/**
 * 智能空调训练数据项类
 * 
 * 该类表示一个训练数据项，包含传感器ID、功率向量、温度和室外温度等信息，
 * 实现了TrainDataItem接口。
 */
public class IntellacTrainDataItem implements TrainDataItem<Float, Float> {

	/**
	 * 数字格式化器，用于格式化输出
	 */
	private final static DecimalFormat DecimalFormat = new DecimalFormat(".0");

	/**
	 * 传感器ID
	 */
	private String sensorId;
	
	/**
	 * 功率向量
	 */
	private PowerVector powerVector;
	
	/**
	 * 温度值
	 */
	private float temperature;
	
	/**
	 * 室外温度
	 */
	private float outsideTemp;

	/**
	 * 构造函数
	 * 
	 * @param sensorId 传感器ID
	 * @param powerVector 功率向量
	 * @param temperature 温度值
	 * @param outsideTemp 室外温度
	 */
	public IntellacTrainDataItem(String sensorId, PowerVector powerVector, float temperature,
			float outsideTemp) {
		this.sensorId = sensorId;
		this.powerVector = powerVector;
		this.temperature = temperature;
		this.outsideTemp = outsideTemp;
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
	 * 获取功率向量
	 * 
	 * @return 功率向量
	 */
	public PowerVector getPowerVector() {
		return powerVector;
	}

	/**
	 * 设置功率向量
	 * 
	 * @param powerVector 功率向量
	 */
	public void setPowerVector(PowerVector powerVector) {
		this.powerVector = powerVector;
	}

	/**
	 * 获取温度值
	 * 
	 * @return 温度值
	 */
	public float getTemperature() {
		return temperature;
	}

	/**
	 * 设置温度值
	 * 
	 * @param temperature 温度值
	 */
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	/**
	 * 获取特征值数组
	 * 
	 * @return 特征值数组（功率值数组）
	 */
	@Override
	public Float[] getFeature() {
		return powerVector.getPowerValueFloatArray();
	}

	/**
	 * 获取结果值
	 * 
	 * @return 结果值（温度值）
	 */
	@Override
	public Float getResult() {
		return temperature;
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
	 * 转换为字符串表示
	 * 
	 * @return 字符串表示
	 */
	@Override
	public String toString() {

		return sensorId + "\t" + DecimalFormat.format(temperature) + "\t"
				+ DecimalFormat.format(outsideTemp) + "\t" + powerVector.toString();
	}

}