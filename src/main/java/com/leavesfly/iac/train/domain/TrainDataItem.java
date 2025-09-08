package com.leavesfly.iac.train.domain;

/**
 * 训练数据项接口
 * 
 * 该接口定义了训练数据项的基本结构，包括特征值和结果值的获取方法。
 * 
 * @param <F> 特征值类型
 * @param <R> 结果值类型
 */
public interface TrainDataItem<F, R> {

	/**
	 * 获取特征值数组
	 * 
	 * @return 特征值数组
	 */
	public F[] getFeature();

	/**
	 * 获取结果值
	 * 
	 * @return 结果值
	 */
	public R getResult();

}