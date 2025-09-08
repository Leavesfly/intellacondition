package com.leavesfly.iac.train.store;

import java.util.Collection;

import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 训练数据集管理器接口
 * 
 * 该接口定义了训练数据集管理器的基本功能，
 * 包括存储训练数据集和根据传感器ID及室外温度获取训练数据集。
 */
public interface TrainDataSetManager {
	/**
	 * 根据传感器ID和室外温度获取训练数据集
	 * 
	 * @param sensorId 传感器ID
	 * @param outsideTemp 室外温度
	 * @return 训练数据集
	 */
	public Collection<IntellacTrainDataItem> fetchTrainDataSetBySensorId(String sensorId,
			float outsideTemp);

	/**
	 * 存储训练数据集
	 * 
	 * @param tarinDataSet 训练数据集
	 */
	public void storeTrainDataSet(Collection<IntellacTrainDataItem> tarinDataSet);

}