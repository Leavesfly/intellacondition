package com.leavesfly.iac.train.trainer.bp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.config.BpAlgorithmConstant;
import com.leavesfly.iac.train.collect.DataCollecter;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.domain.TrainDataItem;
import com.leavesfly.iac.train.store.TrainDataSetManager;
import com.leavesfly.iac.train.trainer.TrainModel;
import com.leavesfly.iac.util.MathUtil;

/**
 * 三层BP神经网络模型
 * 
 * @author LeavesFly
 * 
 */
public class BpnnModel implements TrainModel {

	private final int inputLevelCellNum;
	private final int hiddenLevelCellNum;
	private final int outputLevelCellNum;

	private final float learnRate;
	private final int MaxIterateNum;

	private final float MaxRange;
	private final float MinRange;

	private Cell[] inputLevel;
	private Cell[] hiddenLevel;
	private Cell[] outputLevel;

	private Float[] currentFeature;
	private float currentResult;
	private int currentIterateNum;

	public static TrainModel getIntance() {
		return new BpnnModel();
	}

	private BpnnModel() {
		this.outputLevelCellNum = BpAlgorithmConstant.OUTPUT_LEVEL_CELL_NUM;
		this.hiddenLevelCellNum = BpAlgorithmConstant.HIDDEN_LEVEL_CELL_NUM;
		this.inputLevelCellNum = BpAlgorithmConstant.INPUT_LEVEL_CELL_NUM;
		this.learnRate = BpAlgorithmConstant.LEARN_RATE;
		this.MaxIterateNum = BpAlgorithmConstant.ITERATE_NUM;
		this.MaxRange = BpAlgorithmConstant.MAX_RESULT_NORMAL;
		this.MinRange = BpAlgorithmConstant.MIN_RESULT_NORMAL;

		initCellSet();
		initWeightNet();
	}

	public BpnnModel(int inputLevelCellNum, int outputLevelCellNum) {
		this.outputLevelCellNum = outputLevelCellNum;
		this.hiddenLevelCellNum = BpAlgorithmConstant.determineHiddenLevel(inputLevelCellNum,
				outputLevelCellNum);
		this.inputLevelCellNum = inputLevelCellNum;
		this.learnRate = BpAlgorithmConstant.LEARN_RATE;
		this.MaxIterateNum = BpAlgorithmConstant.ITERATE_NUM;
		this.MaxRange = BpAlgorithmConstant.MAX_RESULT_NORMAL;
		this.MinRange = BpAlgorithmConstant.MIN_RESULT_NORMAL;

		initCellSet();
		initWeightNet();
	}

	public BpnnModel(int inputLevelCellNum, int outputLevelCellNum, float learnRate, int iterateNum) {
		this.outputLevelCellNum = outputLevelCellNum;
		this.hiddenLevelCellNum = BpAlgorithmConstant.determineHiddenLevel(inputLevelCellNum,
				outputLevelCellNum);
		this.inputLevelCellNum = inputLevelCellNum;
		this.learnRate = learnRate;
		this.MaxIterateNum = iterateNum;
		this.MaxRange = BpAlgorithmConstant.MAX_RESULT_NORMAL;
		this.MinRange = BpAlgorithmConstant.MIN_RESULT_NORMAL;

		initCellSet();
		initWeightNet();
	}

	public BpnnModel(int inputLevelCellNum, int hiddenLevelCellNum, int outputLevelCellNum) {
		this.outputLevelCellNum = outputLevelCellNum;
		this.hiddenLevelCellNum = hiddenLevelCellNum;
		this.inputLevelCellNum = inputLevelCellNum;
		this.learnRate = BpAlgorithmConstant.LEARN_RATE;
		this.MaxIterateNum = BpAlgorithmConstant.ITERATE_NUM;
		this.MaxRange = BpAlgorithmConstant.MAX_RESULT_NORMAL;
		this.MinRange = BpAlgorithmConstant.MIN_RESULT_NORMAL;

		initCellSet();
		initWeightNet();
	}

	public BpnnModel(int inputLevelCellNum, int hiddenLevelCellNum, int outputLevelCellNum,
			float learnRate, int iterateNum) {
		this.outputLevelCellNum = outputLevelCellNum;
		this.hiddenLevelCellNum = hiddenLevelCellNum;
		this.inputLevelCellNum = inputLevelCellNum;
		this.learnRate = learnRate;
		this.MaxIterateNum = iterateNum;
		this.MaxRange = BpAlgorithmConstant.MAX_RESULT_NORMAL;
		this.MinRange = BpAlgorithmConstant.MIN_RESULT_NORMAL;

		initCellSet();
		initWeightNet();
	}

	private void initCellSet() {
		inputLevel = new Cell[inputLevelCellNum];
		for (int i = 0; i < inputLevelCellNum; i++) {
			inputLevel[i] = Cell.genInputCell();
		}

		hiddenLevel = new Cell[hiddenLevelCellNum];
		for (int i = 0; i < hiddenLevelCellNum; i++) {
			hiddenLevel[i] = Cell.genHiddenCell(MathUtil.nextFloat(
					BpAlgorithmConstant.EXCURSION_RANDOM_FROM,
					BpAlgorithmConstant.EXCURSION_RANDOM_TO));
		}

		outputLevel = new Cell[outputLevelCellNum];
		for (int i = 0; i < outputLevelCellNum; i++) {
			outputLevel[i] = Cell.genOutputCell(MathUtil.nextFloat(
					BpAlgorithmConstant.EXCURSION_RANDOM_FROM,
					BpAlgorithmConstant.EXCURSION_RANDOM_TO));
		}
	}

	private void initWeightNet() {

		for (int i = 0; i < inputLevelCellNum; i++) {
			for (int j = 0; j < hiddenLevelCellNum; j++) {
				Synapse synapse = new Synapse(inputLevel[i], hiddenLevel[j], MathUtil.nextFloat(
						BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
						BpAlgorithmConstant.WEIGHT_RANDOM_TO));
				inputLevel[i].getOutputSynapseList().add(synapse);
				hiddenLevel[j].getInputSynapseList().add(synapse);
			}
		}

		for (int i = 0; i < hiddenLevelCellNum; i++) {
			for (int j = 0; j < outputLevelCellNum; j++) {
				Synapse synapse = new Synapse(hiddenLevel[i], outputLevel[j], MathUtil.nextFloat(
						BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
						BpAlgorithmConstant.WEIGHT_RANDOM_TO));
				hiddenLevel[i].getOutputSynapseList().add(synapse);
				outputLevel[j].getInputSynapseList().add(synapse);
			}
		}
	}

	/**
	 * 训练模型
	 * 
	 * @param trainDataSet
	 */
	@Override
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet) {
		for (TrainDataItem<Float, Float> trainDataItem : trainDataSet) {
			trainSingleData(trainDataItem);
		}

	}

	private void trainSingleData(TrainDataItem<Float, Float> trainDataItem) {
		initSigleDataTrain(trainDataItem);
		while (true) {
			// 向前传播输入
			feedForward();
			// 反向误差传播
			backPropagation();
			// 网络权重与神经元偏置调整
			adjust();
			// 判断结束
			if (isMeetOverCondition()) {
				break;
			}
			++currentIterateNum;
		}
		System.out.println(currentIterateNum);

	}

	private void initSigleDataTrain(TrainDataItem<Float, Float> trainDataItem) {

		this.currentFeature = trainDataItem.getFeature();
		this.currentResult = (trainDataItem.getResult() - MinRange) / (MaxRange - MinRange);
		currentIterateNum = 0;

		if (currentFeature.length != inputLevel.length) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < inputLevel.length; i++) {
			inputLevel[i].setOutput(currentFeature[i] / AppContextConstant.AIR_CONDITION_MAX_POWER);
		}

	}

	private void feedForward() {

		for (int i = 0; i < hiddenLevel.length; i++) {
			hiddenLevel[i].setOutput(hiddenLevel[i].calOutput());
		}
		for (int i = 0; i < outputLevel.length; i++) {
			outputLevel[i].setOutput(outputLevel[i].calOutput());
		}
	}

	private void backPropagation() {
		for (int i = 0; i < outputLevel.length; i++) {
			outputLevel[i].setCurrentError(outputLevel[i].calOutputError(currentResult));
		}
		for (int i = 0; i < hiddenLevel.length; i++) {
			hiddenLevel[i].setCurrentError(hiddenLevel[i].calError());
		}
	}

	private void adjust() {

		for (int i = 0; i < hiddenLevel.length; i++) {
			List<Synapse> synapseList = hiddenLevel[i].getInputSynapseList();
			for (Synapse synapse : synapseList) {
				synapse.setWeight(synapse.getWeight() + synapse.getBackCell().getOutput()
						* hiddenLevel[i].getCurrentError() * learnRate);
			}
			hiddenLevel[i].setExcursion(hiddenLevel[i].getExcursion()
					+ hiddenLevel[i].getCurrentError() * learnRate);
		}

		for (int i = 0; i < outputLevel.length; i++) {
			List<Synapse> synapseList = outputLevel[i].getInputSynapseList();
			for (Synapse synapse : synapseList) {
				synapse.setWeight(synapse.getWeight() + synapse.getBackCell().getOutput()
						* outputLevel[i].getCurrentError() * learnRate);
			}
			outputLevel[i].setExcursion(outputLevel[i].getExcursion()
					+ outputLevel[i].getCurrentError() * learnRate);
		}
	}

	private boolean isMeetOverCondition() {

		if (currentIterateNum > MaxIterateNum
				|| outputLevel[0].getCurrentError() < BpAlgorithmConstant.MIN_ERROR) {
			return true;
		}
		return false;
	}

	/**
	 * 使用模型
	 * 
	 * @return
	 */
	@Override
	public <T extends Number> float useMode(T[] feature) {
		return calUseBpMode((Float[]) feature);
	}

	private float calUseBpMode(Float[] feature) {
		currentFeature = feature;
		if (currentFeature.length != inputLevel.length) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < inputLevel.length; i++) {
			inputLevel[i].setOutput(currentFeature[i] / AppContextConstant.AIR_CONDITION_MAX_POWER);
		}
		feedForward();

		return outputLevel[0].getOutput() * (MaxRange - MinRange) + MinRange;
	}

	/**
	 * 
	 * @author LeavesFly
	 * 
	 *         细胞体
	 */
	private static class Cell {
		private List<Synapse> inputSynapseList;
		private List<Synapse> outputSynapseList;

		private float excursion;
		private float output;
		private float currentError;

		/**
		 * 
		 * @return
		 */
		public static Cell genInputCell() {
			Cell cell = new Cell();
			cell.setExcursion(0f);
			cell.setInputSynapseList(Collections.<Synapse> emptyList());
			cell.setOutputSynapseList(new ArrayList<Synapse>());
			return cell;
		}

		/**
		 * 
		 * @param excursion
		 * @return
		 */
		public static Cell genHiddenCell(float excursion) {
			Cell cell = new Cell();
			cell.setExcursion(excursion);
			cell.setInputSynapseList(new ArrayList<Synapse>());
			cell.setOutputSynapseList(new ArrayList<Synapse>());
			return cell;
		}

		/**
		 * 
		 * @param excursion
		 * @return
		 */
		public static Cell genOutputCell(float excursion) {
			Cell cell = new Cell();
			cell.setExcursion(excursion);
			cell.setInputSynapseList(new ArrayList<Synapse>());
			cell.setOutputSynapseList(Collections.<Synapse> emptyList());
			return cell;
		}

		/**
		 * 
		 * @return
		 */
		public float calOutput() {
			float result = 0f;
			for (Synapse backSynapse : inputSynapseList) {
				result += backSynapse.getBackCell().getOutput() * backSynapse.getWeight();
			}
			result += excursion;
			result = (float) (1 / (1 + Math.exp(-result)));
			return result;
		}

		public float calError() {
			float result = 0f;
			for (Synapse frontSynapse : outputSynapseList) {
				result += frontSynapse.getFrontCell().getCurrentError() * frontSynapse.getWeight();
			}
			result = output * (1.0f - output) * result;
			return result;
		}

		public float calOutputError(float currentResult) {
			return output * (1.0f - output) * (currentResult - output);
		}

		public List<Synapse> getInputSynapseList() {
			return inputSynapseList;
		}

		private void setInputSynapseList(List<Synapse> inputSynapseList) {
			this.inputSynapseList = inputSynapseList;
		}

		public List<Synapse> getOutputSynapseList() {
			return outputSynapseList;
		}

		private void setOutputSynapseList(List<Synapse> outPutSynapseList) {
			this.outputSynapseList = outPutSynapseList;
		}

		public float getExcursion() {
			return excursion;
		}

		public void setExcursion(float excursion) {
			this.excursion = excursion;
		}

		public void setOutput(float output) {
			this.output = output;
		}

		public float getOutput() {
			return output;
		}

		public float getCurrentError() {
			return currentError;
		}

		public void setCurrentError(float currentError) {
			this.currentError = currentError;
		}

	}

	/**
	 * 
	 * @author LeavesFly
	 * 
	 *         突触
	 */
	private static class Synapse {
		// 特征输出方向
		private Cell backCell;
		private Cell frontCell;
		private float weight;

		public Synapse(Cell backCell, Cell frontCell, float weight) {
			this.backCell = backCell;
			this.frontCell = frontCell;
			this.weight = weight;
		}

		public Cell getBackCell() {
			return backCell;
		}

		public Cell getFrontCell() {
			return frontCell;
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			this.weight = weight;
		}

	}

	public static void main(String[] args) {
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);

		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();

		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();

		trainDataSetManager.storeTrainDataSet(trainDatas);

		Collection<IntellacTrainDataItem> datas = trainDataSetManager.fetchTrainDataSetBySensorId(
				"1", AppContextConstant.OUTSIDE_TEMP);
		TrainModel trainModel = BpnnModel.getIntance();
		trainModel.train(datas);

		for (IntellacTrainDataItem item : datas) {
			System.out.println(item);
			System.out.println(trainModel.useMode(item.getFeature()));
		}
	}

}
