package com.leavesfly.iac.train.trainer;

import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 训练任务抽象类
 * 
 * 该类实现了Runnable接口，表示一个训练任务，用于在独立线程中训练特定传感器的模型。
 * 使用CyclicBarrier实现多线程同步，确保所有训练任务完成后才能继续执行。
 */
public abstract class TrainTask implements Runnable {

	/**
	 * 同步栅栏，用于多线程同步
	 */
	private final CyclicBarrier barrier;
	
	/**
	 * 室外温度
	 */
	private float outsideTemp;
	
	/**
	 * 传感器ID
	 */
	private String sensorId;
	
	/**
	 * 训练数据集
	 */
	private Collection<IntellacTrainDataItem> trainDataSet;
	
	/**
	 * 拟合函数集合
	 */
	private Collection<PtFitFunc> fitFuncSet;

	/**
	 * 构造函数
	 * 
	 * @param barrier 同步栅栏
	 * @param trainDataSet 训练数据集
	 * @param outsideTemp 室外温度
	 * @param sensorId 传感器ID
	 * @param fitFuncSet 拟合函数集合
	 */
	public TrainTask(CyclicBarrier barrier, Collection<IntellacTrainDataItem> trainDataSet,
			float outsideTemp, String sensorId, Collection<PtFitFunc> fitFuncSet) {
		this.barrier = barrier;
		this.trainDataSet = trainDataSet;
		this.outsideTemp = outsideTemp;
		this.sensorId = sensorId;
		this.fitFuncSet = fitFuncSet;
	}

	/**
	 * 执行训练任务
	 * 
	 * 获取训练模型实例，使用训练数据集训练模型，
	 * 创建功率-温度拟合函数并添加到拟合函数集合中，
	 * 最后等待所有训练任务完成。
	 */
	@Override
	public void run() {
		TrainModel trainModel = getTrainModelInstance();
		trainModel.train(trainDataSet);
		PtFitFunc fitFunc = new PtFitFunc(sensorId, outsideTemp, trainModel);
		fitFuncSet.add(fitFunc);
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取训练模型实例的抽象方法
	 * 
	 * 由具体子类实现，返回特定类型的训练模型实例
	 * 
	 * @return 训练模型实例
	 */
	protected abstract TrainModel getTrainModelInstance();

}