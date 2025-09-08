package com.leavesfly.iac.execute.domain;

/**
 * 用户温度范围类
 * 
 * 该类表示用户可接受的温度范围，包含最小值和最大值，
 * 并提供判断某个温度是否在范围内的方法。
 */
public class UserTempRange {
	/**
	 * 温度范围起始值（最小值）
	 */
	private float from;
	
	/**
	 * 温度范围结束值（最大值）
	 */
	private float to;

	/**
	 * 构造函数
	 * 
	 * @param from 温度范围起始值
	 * @param to 温度范围结束值
	 */
	public UserTempRange(float from, float to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * 获取温度范围起始值
	 * 
	 * @return 温度范围起始值
	 */
	public float getFrom() {
		return from;
	}

	/**
	 * 设置温度范围起始值
	 * 
	 * @param from 温度范围起始值
	 */
	public void setFrom(float from) {
		this.from = from;
	}

	/**
	 * 获取温度范围结束值
	 * 
	 * @return 温度范围结束值
	 */
	public float getTo() {
		return to;
	}

	/**
	 * 设置温度范围结束值
	 * 
	 * @param to 温度范围结束值
	 */
	public void setTo(float to) {
		this.to = to;
	}

	/**
	 * 判断指定温度是否在范围内
	 * 
	 * @param temp 待判断的温度值
	 * @return 如果在范围内返回true，否则返回false
	 */
	public boolean isInRange(float temp) {
		if (temp > to || temp < from) {
			return false;
		}
		return true;
	}

}