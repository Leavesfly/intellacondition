package com.leavesfly.iac.train.trainer;

import java.util.Collection;

import com.leavesfly.iac.train.domain.TrainDataItem;

public interface TrainModel {

	/**
	 * 训练模型
	 * 
	 * @param trainDataSet
	 */
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet);

	/**
	 * 使用模型
	 * 
	 * @param parameter
	 * @return
	 */
	public <T extends Number> float useMode(T[] feature);

}
