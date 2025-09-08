package com.leavesfly.iac.domain;

/**
 * 范围值接口
 * 
 * 该接口定义了具有范围限制的值的相关操作，包括值的设置和获取、
 * 范围的设置和获取以及范围检查等功能，并继承了EnableClone接口。
 */
public interface RangeValue extends EnableClone {

	/**
	 * 设置值
	 * 
	 * @param value 值
	 */
	public void setValue(float value);

	/**
	 * 获取值
	 * 
	 * @return 值
	 */
	public float getValue();

	/**
	 * 获取范围起始值
	 * 
	 * @return 范围起始值
	 */
	public float getFrom();

	/**
	 * 设置范围起始值
	 * 
	 * @param from 范围起始值
	 */
	public void setFrom(float from);

	/**
	 * 获取范围结束值
	 * 
	 * @return 范围结束值
	 */
	public float getTo();

	/**
	 * 设置范围结束值
	 * 
	 * @param to 范围结束值
	 */
	public void setTo(float to);

	/**
	 * 判断指定值是否在范围内
	 * 
	 * @param value 待判断的值
	 * @return 如果在范围内返回true，否则返回false
	 */
	public boolean isInRange(float value);
}