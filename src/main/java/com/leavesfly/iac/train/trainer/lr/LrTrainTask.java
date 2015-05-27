package com.leavesfly.iac.train.trainer.lr;

import java.util.Collection;
import java.util.concurrent.CyclicBarrier;

import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.trainer.TrainModel;
import com.leavesfly.iac.train.trainer.TrainTask;

public class LrTrainTask extends TrainTask {

	public LrTrainTask(CyclicBarrier barrier, Collection<IntellacTrainDataItem> trainDataSet,
			float outsideTemp, String sensorId, Collection<PtFitFunc> fitFuncOfPTSet) {
		super(barrier, trainDataSet, outsideTemp, sensorId, fitFuncOfPTSet);
	}

	@Override
	protected TrainModel getTrainModelInstance() {

		return LineRegressModel.getIntance();
	}

}
