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
 * 
 * @author yefei.yf
 *
 */
public class PtMultiThreadTrainer implements PtTrainer {

	public final ExecutorService executorService;
	public final ModelEnum modelSelect;
	public final CyclicBarrier barrier;

	private TrainDataSetManager trainDataSetManager;

	public PtMultiThreadTrainer(TrainDataSetManager trainDataSetManager, ModelEnum modelSelect) {
		executorService = Executors.newCachedThreadPool();
		this.modelSelect = modelSelect;
		barrier = new CyclicBarrier(AppContextConstant.SENSOR_NUM + 1);
		this.trainDataSetManager = trainDataSetManager;
	}

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
