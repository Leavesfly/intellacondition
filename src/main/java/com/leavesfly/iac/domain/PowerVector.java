package com.leavesfly.iac.domain;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 功率向量类
 * 
 * 该类表示一组空调设备的功率值集合，每个元素是一个PowerValue对象，
 * 包含功率值和对应的取值范围。实现了Serializable接口以支持序列化。
 */
public class PowerVector implements Serializable {

	/**
	 * 数字格式化器，用于格式化输出
	 */
	private final static DecimalFormat DecimalFormat = new DecimalFormat(".0");
	private static final long serialVersionUID = 1L;

	/**
	 * 功率值数组
	 */
	private PowerValue[] powerValueVector;
	
	/**
	 * 向量大小
	 */
	private int size;

	/**
	 * 构造函数
	 * 
	 * @param powerValueVector 功率值数组
	 */
	public PowerVector(PowerValue[] powerValueVector) {
		this.powerValueVector = powerValueVector;
		this.size = powerValueVector.length;
	}

	/**
	 * 构造函数
	 * 
	 * @param powerValueArray 功率值浮点数组
	 * @param length 向量长度
	 */
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

	/**
	 * 获取功率值数组
	 * 
	 * @return 功率值数组
	 */
	public PowerValue[] getPowerValueVector() {
		return powerValueVector;
	}

	/**
	 * 设置功率值数组
	 * 
	 * @param powerValueVector 功率值数组
	 */
	public void setPowerValueVector(PowerValue[] powerValueVector) {
		this.powerValueVector = powerValueVector;
	}

	/**
	 * 获取向量大小
	 * 
	 * @return 向量大小
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 设置向量大小
	 * 
	 * @param size 向量大小
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 获取功率值浮点数组
	 * 
	 * @return 功率值浮点数组
	 */
	public Float[] getPowerValueFloatArray() {
		Float[] value = new Float[powerValueVector.length];
		for (int i = 0; i < value.length; i++) {
			value[i] = powerValueVector[i].getValue();
		}
		return value;
	}

	/**
	 * 根据相同范围创建功率向量实例
	 * 
	 * @param from 范围起始值
	 * @param to 范围结束值
	 * @param size 向量大小
	 * @return 功率向量实例
	 */
	public static PowerVector getInstanceBySameRange(float from, float to, int size) {
		PowerValue[] powerValueVector = new PowerValue[size];
		for (int i = 0; i < size; i++) {
			powerValueVector[i] = new PowerValue(new PowerRange(from, to));
		}
		return new PowerVector(powerValueVector);
	}

	/**
	 * 转换为字符串表示
	 * 
	 * @return 字符串表示
	 */
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