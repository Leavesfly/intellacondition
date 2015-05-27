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

public class IntelliAirCondition {

	public static void main(String[] args) {

		IntelliAirCondition intelliAirCondition = new IntelliAirCondition();

		intelliAirCondition.trainPhase();

		intelliAirCondition.schedulePhase();

		intelliAirCondition.displayPhase();
	}

	private void trainPhase() {
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);

		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();

		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();

		trainDataSetManager.storeTrainDataSet(trainDatas);

		PtTrainer ptTrainer = new PtMultiThreadTrainer(trainDataSetManager, ModelEnum.BPWEKA);

		Collection<PtFitFunc> ptFitFuncSet = ptTrainer
				.buildFitFuncSet(AppContextConstant.OUTSIDE_TEMP);
		DataFactory.getInstance().registerFitFunc(ptFitFuncSet);
	}


	private void schedulePhase() {

		final DataFactory dataFactory = DataFactory.getInstance();
		ExecutorService executorService = Executors.newCachedThreadPool();
		final CountDownLatch latch = new CountDownLatch(3);

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
			latch.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executorService.shutdown();

	}

	private void displayPhase() {
		Map<String, EvaluteResult> evaluteResultMap = DataFactory.getInstance()
				.getEvaluteResultMap();
		for (Map.Entry<String, EvaluteResult> entry : evaluteResultMap.entrySet()) {
			System.out.println(entry.getValue().toString());
		}
	}
}
