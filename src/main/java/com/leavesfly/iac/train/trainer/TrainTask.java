package com.leavesfly.iac.train.trainer;

import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

public abstract class TrainTask implements Runnable {

	// 同步栅
	private final CyclicBarrier barrier;
	private float outsideTemp;
	private String sensorId;
	private Collection<IntellacTrainDataItem> trainDataSet;
	private Collection<PtFitFunc> fitFuncSet;

	public TrainTask(CyclicBarrier barrier, Collection<IntellacTrainDataItem> trainDataSet,
			float outsideTemp, String sensorId, Collection<PtFitFunc> fitFuncSet) {
		this.barrier = barrier;
		this.trainDataSet = trainDataSet;
		this.outsideTemp = outsideTemp;
		this.sensorId = sensorId;
		this.fitFuncSet = fitFuncSet;
	}

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

	protected abstract TrainModel getTrainModelInstance();

}
