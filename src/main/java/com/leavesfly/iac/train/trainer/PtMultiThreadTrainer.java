package com.leavesfly.iac.train.trainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.train.PtTrainer;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.store.TrainDataSetManager;
import com.leavesfly.iac.train.trainer.bp.BpTrianTask;
import com.leavesfly.iac.train.trainer.bp.BpWekaTrianTask;
import com.leavesfly.iac.train.trainer.lr.LrTrainTask;

/**
 * 多线程功率-温度训练器类
 * 
 * 该类实现了PtTrainer接口，使用多线程技术并行训练多个传感器的功率-温度模型，
 * 支持多种训练模型（BP神经网络、Weka的BP神经网络、线性回归等）。
 */
public class PtMultiThreadTrainer implements PtTrainer {

	/**
	 * 线程池执行器
	 */
	public final ExecutorService executorService;
	
	/**
	 * 模型选择枚举
	 */
	public final ModelEnum modelSelect;
	
	/**
	 * 同步栅栏，用于多线程同步
	 */
	public final CyclicBarrier barrier;

	/**
	 * 训练数据集管理器
	 */
	private TrainDataSetManager trainDataSetManager;

	/**
	 * 构造函数
	 * 
	 * @param trainDataSetManager 训练数据集管理器
	 * @param modelSelect 模型选择枚举
	 */
	public PtMultiThreadTrainer(TrainDataSetManager trainDataSetManager, ModelEnum modelSelect) {
		executorService = Executors.newCachedThreadPool();
		this.modelSelect = modelSelect;
		barrier = new CyclicBarrier(AppContextConstant.SENSOR_NUM + 1);
		this.trainDataSetManager = trainDataSetManager;
	}

	/**
	 * 构建功率-温度拟合函数集合
	 * 
	 * 为每个传感器并行训练模型，并构建对应的功率-温度拟合函数集合。
	 * 使用CyclicBarrier确保所有训练任务完成后才返回结果。
	 * 
	 * @param outsideTemp 室外温度
	 * @return 功率-温度拟合函数集合
	 */
	@Override
	public Collection<PtFitFunc> buildFitFuncSet(float outsideTemp) {

		barrier.reset();
		DataFactory dataFactory = DataFactory.getInstance();
		Set<String> sensorIdSet = dataFactory.getSensorIdSet();

		Collection<PtFitFunc> fitFuncSet = Collections
				.synchronizedCollection(new HashSet<PtFitFunc>());
		for (String sensorId : sensorIdSet) {

			Collection<IntellacTrainDataItem> trainDataSet = trainDataSetManager
					.fetchTrainDataSetBySensorId(sensorId, outsideTemp);
			TrainTask trainTask = null;
			switch (modelSelect) {
			case BPNN:
				trainTask = new BpTrianTask(barrier, trainDataSet, outsideTemp, sensorId,
						fitFuncSet);
				break;

			case BPWEKA:
				trainTask = new BpWekaTrianTask(barrier, trainDataSet, outsideTemp, sensorId,
						fitFuncSet);
				break;

			case LR:
				trainTask = new LrTrainTask(barrier, trainDataSet, outsideTemp, sensorId,
						fitFuncSet);
				break;
			}
			executorService.submit(trainTask);
		}

		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

		executorService.shutdown();
		return fitFuncSet;
	}
}