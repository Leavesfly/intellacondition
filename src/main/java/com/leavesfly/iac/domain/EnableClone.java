package com.leavesfly.iac.domain;

/**
 * 可克隆接口
 * 
 * 该接口有别于JDK的Cloneable接口，定义了clone方法，
 * 用于支持对象的克隆操作。
 */
public interface EnableClone {

	/**
	 * 克隆方法
	 * 
	 * @return 克隆后的对象
	 */
	public Object clone();
}