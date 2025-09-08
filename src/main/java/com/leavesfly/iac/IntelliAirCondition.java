package com.leavesfly.iac;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.config.PsoAlgorithmConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.evalute.Evaluator;
import com.leavesfly.iac.evalute.EvaluteResult;
import com.leavesfly.iac.evalute.Solution;
import com.leavesfly.iac.evalute.SolutionBuilder;
import com.leavesfly.iac.execute.PowerScheduler;
import com.leavesfly.iac.execute.scheduler.PsoPowerScheduler;
import com.leavesfly.iac.train.PtTrainer;
import com.leavesfly.iac.train.collect.DataCollecter;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.store.TrainDataSetManager;
import com.leavesfly.iac.train.trainer.ModelEnum;
import com.leavesfly.iac.train.trainer.PtMultiThreadTrainer;

/**
 * 智能空调仿真平台主类
 * 
 * 该类负责协调整个仿真流程，包括：
 * 1. 训练阶段 - 使用历史数据训练温度预测模型
 * 2. 调度阶段 - 使用PSO算法进行空调功率调度优化
 * 3. 展示阶段 - 输出评估结果
 */
public class IntelliAirCondition {

	/**
	 * 程序入口点
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {

		IntelliAirCondition intelliAirCondition = new IntelliAirCondition();

		// 执行训练阶段
		intelliAirCondition.trainPhase();

		// 执行调度阶段
		intelliAirCondition.schedulePhase();

		// 执行展示阶段
		intelliAirCondition.displayPhase();
	}

	/**
	 * 训练阶段
	 * 
	 * 该阶段负责：
	 * 1. 收集训练数据
	 * 2. 存储训练数据集
	 * 3. 使用训练数据构建温度预测模型
	 */
	private void trainPhase() {
		// 获取数据收集器实例
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);

		// 从文本文件中收集训练数据
		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();

		// 获取训练数据集管理器
		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();

		// 存储训练数据集
		trainDataSetManager.storeTrainDataSet(trainDatas);

		// 创建多线程训练器，使用BP神经网络模型
		PtTrainer ptTrainer = new PtMultiThreadTrainer(trainDataSetManager, ModelEnum.BPWEKA);

		// 构建温度预测函数集
		Collection<PtFitFunc> ptFitFuncSet = ptTrainer
				.buildFitFuncSet(AppContextConstant.OUTSIDE_TEMP);
		
		// 注册温度预测函数到数据工厂
		DataFactory.getInstance().registerFitFunc(ptFitFuncSet);
	}


	/**
	 * 调度阶段
	 * 
	 * 该阶段负责：
	 * 1. 并行执行多种调度算法
	 * 2. 使用PSO算法进行功率调度优化
	 * 3. 评估调度结果
	 */
	private void schedulePhase() {

		final DataFactory dataFactory = DataFactory.getInstance();
		
		// 创建线程池用于并行执行任务
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		// 创建倒计时锁存器，等待所有任务完成
		final CountDownLatch latch = new CountDownLatch(3);

		// 提交第一个任务：基于用户期望温度的解决方案评估
		executorService.submit(new Runnable() {
			@Override
			public void run() {

				Map<String, Float> userWantTempMap = dataFactory.getUserWantTempMap();
				for (Map.Entry<String, Float> entry : userWantTempMap.entrySet()) {
					Solution solution = SolutionBuilder.buildSolution(
							AppContextConstant.SOLUTIN_NAME_PREFIX + entry.getKey(),
							entry.getValue());
					EvaluteResult evaluteResult = Evaluator.transform(solution);
					dataFactory.addEvaluteResult(evaluteResult);
				}
				latch.countDown();
			}
		});

		// 提交第二个任务：标准PSO算法功率调度
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				PowerScheduler powerScheduler = new PsoPowerScheduler(
						PsoAlgorithmConstant.PSO_INIT_PARTICLE_NUM, Boolean.FALSE);
				PowerVector powerVector = powerScheduler.schedule();
				Solution solution = SolutionBuilder.buildSolution(
						AppContextConstant.SOLUTIN_NAME_PREFIX
								+ AppContextConstant.SOLUTIN_NAME_PSO_SUFFIX, powerVector);
				EvaluteResult evaluteResult = Evaluator.transform(solution);
				dataFactory.addEvaluteResult(evaluteResult);
				latch.countDown();
			}
		});

		// 提交第三个任务：混沌PSO算法功率调度
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				PowerScheduler powerScheduler = new PsoPowerScheduler(
						PsoAlgorithmConstant.PSO_INIT_PARTICLE_NUM, Boolean.TRUE);
				PowerVector powerVector = powerScheduler.schedule();
				Solution solution = SolutionBuilder.buildSolution(
						AppContextConstant.SOLUTIN_NAME_PREFIX
								+ AppContextConstant.SOLUTIN_NAME_PSO_CHAOS_SUFFIX, powerVector);
				EvaluteResult evaluteResult = Evaluator.transform(solution);
				dataFactory.addEvaluteResult(evaluteResult);
				latch.countDown();
			}
		});

		try {
			// 等待所有任务完成
			latch.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 关闭线程池
		executorService.shutdown();

	}

	/**
	 * 展示阶段
	 * 
	 * 该阶段负责：
	 * 1. 获取所有评估结果
	 * 2. 输出评估结果到控制台
	 */
	private void displayPhase() {
		// 获取数据工厂中的评估结果映射
		Map<String, EvaluteResult> evaluteResultMap = DataFactory.getInstance()
				.getEvaluteResultMap();
		
		// 遍历并打印所有评估结果
		for (Map.Entry<String, EvaluteResult> entry : evaluteResultMap.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
}