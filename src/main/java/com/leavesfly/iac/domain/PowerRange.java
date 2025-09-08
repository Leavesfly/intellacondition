package com.leavesfly.iac.domain;

import java.io.Serializable;

/**
 * 功率范围类
 * 
 * 该类表示功率的取值范围，包含最小值和最大值，
 * 并提供判断某个值是否在范围内以及生成初始功率值的方法。
 */
public class PowerRange implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 范围起始值（最小值）
	 */
	private float from;
	
	/**
	 * 范围结束值（最大值）
	 */
	private float to;

	/**
	 * 拷贝构造函数
	 * 
	 * @param powerRange 要拷贝的功率范围对象
	 */
	public PowerRange(PowerRange powerRange) {
		from = powerRange.from;
		to = powerRange.to;
	}

	/**
	 * 构造函数
	 * 
	 * @param from 范围起始值
	 * @param to 范围结束值
	 */
	public PowerRange(float from, float to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * 获取范围起始值
	 * 
	 * @return 范围起始值
	 */
	public float getFrom() {
		return from;
	}

	/**
	 * 设置范围起始值
	 * 
	 * @param from 范围起始值
	 */
	public void setFrom(float from) {
		this.from = from;
	}

	/**
	 * 获取范围结束值
	 * 
	 * @return 范围结束值
	 */
	public float getTo() {
		return to;
	}

	/**
	 * 设置范围结束值
	 * 
	 * @param to 范围结束值
	 */
	public void setTo(float to) {
		this.to = to;
	}

	/**
	 * 判断指定值是否在范围内
	 * 
	 * @param value 待判断的值
	 * @return 如果在范围内返回true，否则返回false
	 */
	public boolean isInRange(float value) {
		if (value > to || value < from) {
			return false;
		}
		return true;
	}

	/**
	 * 生成初始功率值
	 * 
	 * @return 基于当前范围的初始功率值对象
	 */
	public PowerValue genInitValue() {
		PowerValue powerValue = new PowerValue(new PowerRange(this));
		return powerValue;
	}

}