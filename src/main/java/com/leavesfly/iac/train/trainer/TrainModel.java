package com.leavesfly.iac.train.trainer;

import java.util.Collection;

import com.leavesfly.iac.train.domain.TrainDataItem;

/**
 * 训练模型接口
 * 
 * 该接口定义了训练模型的基本功能，包括模型训练和模型使用两个核心方法。
 */
public interface TrainModel {

	/**
	 * 训练模型
	 * 
	 * 使用训练数据集训练模型参数
	 * 
	 * @param trainDataSet 训练数据集
	 * @param <T> 训练数据项类型，特征和结果都为Float类型
	 */
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet);

	/**
	 * 使用模型进行预测
	 * 
	 * 根据输入特征值使用训练好的模型进行预测
	 * 
	 * @param feature 特征值数组
	 * @param <T> 特征值类型，继承自Number
	 * @return 预测结果
	 */
	public <T extends Number> float useMode(T[] feature);

}