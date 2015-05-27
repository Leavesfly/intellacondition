package com.leavesfly.iac.train.store;

import java.util.Collection;

import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 
 * @author LeavesFly
 *
 */
public interface TrainDataSetManager {
	/**
	 * 根据节点id和外部温度获取训练数据集
	 * 
	 * @param sensorId
	 * @param outsideTemp
	 * @return
	 */
	public Collection<IntellacTrainDataItem> fetchTrainDataSetBySensorId(String sensorId,
			float outsideTemp);

	/**
	 * 存储训练数据集
	 * 
	 * @param tarinDataSet
	 */
	public void storeTrainDataSet(Collection<IntellacTrainDataItem> tarinDataSet);

}
