package com.leavesfly.iac.train.trainer.bp;

import java.util.Collection;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.config.BpAlgorithmConstant;
import com.leavesfly.iac.train.collect.DataCollecter;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.domain.TrainDataItem;
import com.leavesfly.iac.train.store.TrainDataSetManager;
import com.leavesfly.iac.train.trainer.TrainModel;

public class BpWekaModel implements TrainModel {

	private Instances instances;
	private MultilayerPerceptron multilayerPerceptron;

	private BpWekaModel() {
		init();
	}

	private void init() {
		FastVector attributes = new FastVector();
		for (int i = 0; i <= AppContextConstant.AIR_CONDITION_NUM; i++) {
			attributes.addElement(new Attribute("" + i));
		}
		instances = new Instances("bp_weka", attributes, 100);
		instances.setClassIndex(instances.numAttributes() - 1);

		multilayerPerceptron = new MultilayerPerceptron();
		multilayerPerceptron.setGUI(false);
		multilayerPerceptron.setAutoBuild(true);
		multilayerPerceptron.setDebug(false);
		multilayerPerceptron.setDecay(true);
		multilayerPerceptron.setHiddenLayers("i");
		multilayerPerceptron.setLearningRate(0.2f);
		multilayerPerceptron.setMomentum(0.8f);
		multilayerPerceptron.setNormalizeAttributes(true);
		multilayerPerceptron.setNormalizeNumericClass(false);
		multilayerPerceptron.setReset(false);
		multilayerPerceptron.setSeed(0);
		multilayerPerceptron.setTrainingTime(BpAlgorithmConstant.ITERATE_NUM);
		multilayerPerceptron.setNominalToBinaryFilter(true);
	}

	@Override
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet) {
		
		add2Instances(trainDataSet);
		try {
			multilayerPerceptron.buildClassifier(instances);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("multilayerPerceptron.buildClassifier exception!");
		}
	}

	@Override
	public <T extends Number> float useMode(T[] feature) {

		Instance instance = new Instance(AppContextConstant.AIR_CONDITION_NUM);
		for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
			instance.setValue(i, feature[i].floatValue());
		}
		float result = 0.0f;
		try {
			result = (float) multilayerPerceptron.classifyInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("multilayerPerceptron.classifyInstance exception!");
		}
		return result;
	}

	private <T extends TrainDataItem<Float, Float>> void add2Instances(Collection<T> trainDataSet) {
		Instance instance = null;
		for (TrainDataItem<Float, Float> dataItem : trainDataSet) {
			instance = new Instance(AppContextConstant.AIR_CONDITION_NUM + 1);
			for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
				instance.setValue(i, dataItem.getFeature()[i]);
			}
			instance.setValue(AppContextConstant.AIR_CONDITION_NUM, dataItem.getResult());
			instances.add(instance);
		}
	}

	public static TrainModel getIntance() {
		return new BpWekaModel();
	}

	public static void main(String[] args) {
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);
		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();
		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();
		trainDataSetManager.storeTrainDataSet(trainDatas);
		Collection<IntellacTrainDataItem> datas = trainDataSetManager.fetchTrainDataSetBySensorId(
				"1", AppContextConstant.OUTSIDE_TEMP);
		TrainModel trainModel = BpWekaModel.getIntance();
		trainModel.train(datas);

		for (IntellacTrainDataItem item : datas) {
			System.out.println(item);
			System.out.println(trainModel.useMode(item.getFeature()));
		}

	}

}
