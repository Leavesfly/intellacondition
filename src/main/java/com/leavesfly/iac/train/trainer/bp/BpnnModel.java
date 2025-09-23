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
 * 三层BP神经网络模型实现类
 * 
 * <p>该类实现了标准的三层BP（Back Propagation）神经网络，包含输入层、隐藏层和输出层。
 * 主要用于智能空调系统中的功率-温度关系建模和预测。</p>
 * 
 * <h3>网络结构特点：</h3>
 * <ul>
 *   <li>输入层：接收空调功率数据作为特征</li>
 *   <li>隐藏层：使用Sigmoid激活函数，节点数通过经验公式自动计算</li>
 *   <li>输出层：单节点输出，预测目标温度值</li>
 * </ul>
 * 
 * <h3>训练策略：</h3>
 * <ul>
 *   <li>批量训练：采用轮次(epoch)训练方式，每轮打乱数据顺序</li>
 *   <li>自适应学习率：根据训练进度动态调整学习率</li>
 *   <li>早停机制：监控误差变化，防止过拟合</li>
 *   <li>数值稳定性：添加溢出保护，确保训练稳定</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 创建模型实例
 * TrainModel model = BpnnModel.getIntance();
 * 
 * // 训练模型
 * model.train(trainDataSet);
 * 
 * // 使用模型预测
 * float prediction = model.useMode(inputFeatures);
 * }</pre>
 * 
 * @author LeavesFly
 * @version 2.0
 * @since 1.0
 */
public class BpnnModel implements TrainModel {

	// ================== 网络结构参数 ==================
	/** 输入层神经元数量（等于空调数量） */
	private final int inputLevelCellNum;
	
	/** 隐藏层神经元数量（通过经验公式计算得出） */
	private final int hiddenLevelCellNum;
	
	/** 输出层神经元数量（固定为1，输出预测温度） */
	private final int outputLevelCellNum;

	// ================== 训练超参数 ==================
	/** 学习率，控制权重更新的步长 */
	private final float learnRate;
	
	/** 最大迭代次数，防止训练时间过长 */
	private final int MaxIterateNum;

	// ================== 数据归一化参数 ==================
	/** 输出数据的最大值（用于反归一化） */
	private final float MaxRange;
	
	/** 输出数据的最小值（用于反归一化） */
	private final float MinRange;

	// ================== 网络结构实例 ==================
	/** 输入层神经元数组 */
	private Cell[] inputLevel;
	
	/** 隐藏层神经元数组 */
	private Cell[] hiddenLevel;
	
	/** 输出层神经元数组 */
	private Cell[] outputLevel;

	// ================== 训练过程变量 ==================
	/** 当前训练样本的特征值数组 */
	private Float[] currentFeature;
	
	/** 当前训练样本的目标值（已归一化） */
	private float currentResult;
	private int currentIterateNum;

	/**
	 * 获取BP神经网络模型的单例实例（使用默认配置）
	 * 
	 * <p>该方法创建一个使用默认配置参数的BP神经网络实例：</p>
	 * <ul>
	 *   <li>输入层节点数：由空调数量决定</li>
	 *   <li>隐藏层节点数：通过经验公式自动计算</li>
	 *   <li>输出层节点数：1个（预测温度）</li>
	 *   <li>学习率：0.6</li>
	 *   <li>最大迭代次数：1000次</li>
	 * </ul>
	 * 
	 * @return BP神经网络模型实例
	 */
	public static TrainModel getIntance() {
		return new BpnnModel();
	}

	/**
	 * 默认构造函数，使用默认配置
	 */
	private BpnnModel() {
		this(BpAlgorithmConstant.INPUT_LEVEL_CELL_NUM,
			 BpAlgorithmConstant.HIDDEN_LEVEL_CELL_NUM,
			 BpAlgorithmConstant.OUTPUT_LEVEL_CELL_NUM,
			 BpAlgorithmConstant.LEARN_RATE,
			 BpAlgorithmConstant.ITERATE_NUM);
	}

	/**
	 * 指定输入和输出层节点数的构造函数
	 * 
	 * @param inputLevelCellNum 输入层节点数
	 * @param outputLevelCellNum 输出层节点数
	 */
	public BpnnModel(int inputLevelCellNum, int outputLevelCellNum) {
		this(inputLevelCellNum,
			 BpAlgorithmConstant.determineHiddenLevel(inputLevelCellNum, outputLevelCellNum),
			 outputLevelCellNum,
			 BpAlgorithmConstant.LEARN_RATE,
			 BpAlgorithmConstant.ITERATE_NUM);
	}

	/**
	 * 指定输入和输出层节点数以及学习参数的构造函数
	 * 
	 * @param inputLevelCellNum 输入层节点数
	 * @param outputLevelCellNum 输出层节点数
	 * @param learnRate 学习率
	 * @param iterateNum 最大迭代次数
	 */
	public BpnnModel(int inputLevelCellNum, int outputLevelCellNum, float learnRate, int iterateNum) {
		this(inputLevelCellNum,
			 BpAlgorithmConstant.determineHiddenLevel(inputLevelCellNum, outputLevelCellNum),
			 outputLevelCellNum,
			 learnRate,
			 iterateNum);
	}

	/**
	 * 指定所有网络结构参数的构造函数
	 * 
	 * @param inputLevelCellNum 输入层节点数
	 * @param hiddenLevelCellNum 隐藏层节点数
	 * @param outputLevelCellNum 输出层节点数
	 */
	public BpnnModel(int inputLevelCellNum, int hiddenLevelCellNum, int outputLevelCellNum) {
		this(inputLevelCellNum, hiddenLevelCellNum, outputLevelCellNum,
			 BpAlgorithmConstant.LEARN_RATE, BpAlgorithmConstant.ITERATE_NUM);
	}

	/**
	 * 最全面的构造函数，所有其他构造函数最终都会调用这个
	 * 
	 * @param inputLevelCellNum 输入层节点数
	 * @param hiddenLevelCellNum 隐藏层节点数
	 * @param outputLevelCellNum 输出层节点数
	 * @param learnRate 学习率
	 * @param iterateNum 最大迭代次数
	 */
	public BpnnModel(int inputLevelCellNum, int hiddenLevelCellNum, int outputLevelCellNum,
			float learnRate, int iterateNum) {
		// 参数验证
		if (inputLevelCellNum <= 0 || hiddenLevelCellNum <= 0 || outputLevelCellNum <= 0) {
			throw new IllegalArgumentException("网络层节点数必须大于0");
		}
		if (learnRate <= 0 || learnRate > 1) {
			throw new IllegalArgumentException("学习率必须在(0,1]范围内");
		}
		if (iterateNum <= 0) {
			throw new IllegalArgumentException("迭代次数必须大于0");
		}
		
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

	/**
	 * 初始化神经网络的各层神经元
	 * 
	 * <p>该方法负责创建和初始化三层神经网络的所有神经元：</p>
	 * <ul>
	 *   <li>输入层：创建输入神经元，偏置设为0（不参与计算）</li>
	 *   <li>隐藏层：创建隐藏神经元，随机初始化偏置值</li>
	 *   <li>输出层：创建输出神经元，随机初始化偏置值</li>
	 * </ul>
	 * 
	 * <p>偏置值的随机初始化范围为[-0.5, 0.5]，有助于打破对称性，
	 * 避免所有神经元学习到相同的特征。</p>
	 */
	private void initCellSet() {
		// 初始化输入层神经元
		inputLevel = new Cell[inputLevelCellNum];
		for (int i = 0; i < inputLevelCellNum; i++) {
			inputLevel[i] = Cell.genInputCell();
		}

		// 初始化隐藏层神经元，随机设置偏置值
		hiddenLevel = new Cell[hiddenLevelCellNum];
		for (int i = 0; i < hiddenLevelCellNum; i++) {
			hiddenLevel[i] = Cell.genHiddenCell(MathUtil.nextFloat(
					BpAlgorithmConstant.EXCURSION_RANDOM_FROM,
					BpAlgorithmConstant.EXCURSION_RANDOM_TO));
		}

		// 初始化输出层神经元，随机设置偏置值
		outputLevel = new Cell[outputLevelCellNum];
		for (int i = 0; i < outputLevelCellNum; i++) {
			outputLevel[i] = Cell.genOutputCell(MathUtil.nextFloat(
					BpAlgorithmConstant.EXCURSION_RANDOM_FROM,
					BpAlgorithmConstant.EXCURSION_RANDOM_TO));
		}
	}

	/**
	 * 初始化神经网络的权重连接
	 * 
	 * <p>该方法建立神经网络各层之间的全连接结构，并随机初始化权重：</p>
	 * <ul>
	 *   <li>输入层到隐藏层：建立全连接，每个输入神经元连接到所有隐藏神经元</li>
	 *   <li>隐藏层到输出层：建立全连接，每个隐藏神经元连接到所有输出神经元</li>
	 * </ul>
	 * 
	 * <p>权重的随机初始化范围为[-1.0, 1.0]，这个范围能够：</p>
	 * <ul>
	 *   <li>避免梯度消失问题（权重不会太小）</li>
	 *   <li>避免梯度爆炸问题（权重不会太大）</li>
	 *   <li>保证训练初期的梯度传播效果</li>
	 * </ul>
	 * 
	 * <p>每个突触(Synapse)对象同时被添加到前一层神经元的输出列表和
	 * 后一层神经元的输入列表中，形成双向引用关系。</p>
	 */
	private void initWeightNet() {
		// 建立输入层到隐藏层的全连接
		for (int i = 0; i < inputLevelCellNum; i++) {
			for (int j = 0; j < hiddenLevelCellNum; j++) {
				// 创建突触连接，随机初始化权重
				Synapse synapse = new Synapse(inputLevel[i], hiddenLevel[j], MathUtil.nextFloat(
						BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
						BpAlgorithmConstant.WEIGHT_RANDOM_TO));
				// 将突触添加到相应神经元的连接列表中
				inputLevel[i].getOutputSynapseList().add(synapse);
				hiddenLevel[j].getInputSynapseList().add(synapse);
			}
		}

		// 建立隐藏层到输出层的全连接
		for (int i = 0; i < hiddenLevelCellNum; i++) {
			for (int j = 0; j < outputLevelCellNum; j++) {
				// 创建突触连接，随机初始化权重
				Synapse synapse = new Synapse(hiddenLevel[i], outputLevel[j], MathUtil.nextFloat(
						BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
						BpAlgorithmConstant.WEIGHT_RANDOM_TO));
				// 将突触添加到相应神经元的连接列表中
				hiddenLevel[i].getOutputSynapseList().add(synapse);
				outputLevel[j].getInputSynapseList().add(synapse);
			}
		}
	}

	/**
	 * 训练模型 - 改进版本使用批量训练策略
	 * 
	 * @param trainDataSet 训练数据集
	 */
	@Override
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet) {
		List<TrainDataItem<Float, Float>> dataList = new ArrayList<>(trainDataSet);
		trainWithBatchStrategy(dataList);
	}

	/**
	 * 使用批量训练策略训练模型
	 * 
	 * @param trainDataList 训练数据列表
	 */
	private void trainWithBatchStrategy(List<TrainDataItem<Float, Float>> trainDataList) {
		if (trainDataList.isEmpty()) {
			return;
		}

		double currentLearningRate = learnRate;
		double previousError = Double.MAX_VALUE;
		int noImprovementCount = 0;
		final int EARLY_STOP_PATIENCE = 10; // 早停容忍度
		
		for (int epoch = 0; epoch < MaxIterateNum; epoch++) {
			// 打乱训练数据
			Collections.shuffle(trainDataList);
			
			double totalError = 0.0;
			int sampleCount = 0;
			
			// 对每个样本进行一次前向传播和反向传播
			for (TrainDataItem<Float, Float> item : trainDataList) {
				double sampleError = trainSingleEpoch(item, currentLearningRate);
				totalError += sampleError;
				sampleCount++;
			}
			
			double avgError = totalError / sampleCount;
			
			// 早停检查
			if (avgError < BpAlgorithmConstant.MIN_ERROR) {
				System.out.println("训练收敛，第 " + (epoch + 1) + " 轮，平均误差: " + avgError);
				break;
			}
			
			// 学习率衰减和早停
			if (avgError >= previousError) {
				noImprovementCount++;
				if (noImprovementCount > EARLY_STOP_PATIENCE) {
					System.out.println("训练早停，第 " + (epoch + 1) + " 轮，平均误差: " + avgError);
					break;
				}
				// 学习率衰减
				currentLearningRate *= 0.95;
			} else {
				noImprovementCount = 0;
			}
			
			previousError = avgError;
			
			// 每100轮输出一次进度
			if ((epoch + 1) % 100 == 0) {
				System.out.println("第 " + (epoch + 1) + " 轮训练，平均误差: " + avgError + 
					", 学习率: " + currentLearningRate);
			}
		}
	}

	/**
	 * 单次训练迭代（方法名改名以反映新的训练策略）
	 * 
	 * @param trainDataItem 训练数据项
	 * @param learningRate 当前学习率
	 * @return 当前样本的误差
	 */
	private double trainSingleEpoch(TrainDataItem<Float, Float> trainDataItem, double learningRate) {
		initSingleDataTrain(trainDataItem);
		
		// 前向传播
		feedForward();
		
		// 计算实际误差
		float predictedValue = outputLevel[0].getOutput() * (MaxRange - MinRange) + MinRange;
		float actualValue = trainDataItem.getResult();
		double sampleError = Math.pow(predictedValue - actualValue, 2);
		
		// 反向传播
		backPropagation();
		
		// 权重调整（使用传入的学习率）
		adjust(learningRate);
		
		return sampleError;
	}

	private void initSingleDataTrain(TrainDataItem<Float, Float> trainDataItem) {
		this.currentFeature = trainDataItem.getFeature();
		// 改进归一化策略，保持一致性
		this.currentResult = (trainDataItem.getResult() - MinRange) / (MaxRange - MinRange);

		if (currentFeature.length != inputLevel.length) {
			throw new IllegalArgumentException("输入特征维度与输入层节点数不匹配");
		}

		// 改进输入归一化，使用最大最小值归一化
		for (int i = 0; i < inputLevel.length; i++) {
			float normalizedInput = (currentFeature[i] - AppContextConstant.AIR_CONDITION_MIN_POWER) / 
				(AppContextConstant.AIR_CONDITION_MAX_POWER - AppContextConstant.AIR_CONDITION_MIN_POWER);
			inputLevel[i].setOutput(normalizedInput);
		}
	}

	/**
	 * 执行前向传播计算
	 * 
	 * <p>前向传播是神经网络的信息流向过程，信号从输入层依次传递到输出层：</p>
	 * <ol>
	 *   <li>输入层：直接接收外部输入，不进行计算</li>
	 *   <li>隐藏层：计算加权输入和，应用Sigmoid激活函数</li>
	 *   <li>输出层：计算加权输入和，应用Sigmoid激活函数</li>
	 * </ol>
	 * 
	 * <p>计算公式：output = sigmoid(Σ(weight_i × input_i) + bias)</p>
	 * <p>其中sigmoid(x) = 1 / (1 + e^(-x))</p>
	 * 
	 * <p>注意：输入层的输出值在训练开始前已经设置，这里不需要重新计算。</p>
	 */
	private void feedForward() {
		// 计算隐藏层各神经元的输出值
		for (int i = 0; i < hiddenLevel.length; i++) {
			hiddenLevel[i].setOutput(hiddenLevel[i].calOutput());
		}
		
		// 计算输出层各神经元的输出值
		for (int i = 0; i < outputLevel.length; i++) {
			outputLevel[i].setOutput(outputLevel[i].calOutput());
		}
	}

	/**
	 * 执行反向传播计算
	 * 
	 * <p>反向传播是BP算法的核心，用于计算各层神经元的误差梯度：</p>
	 * <ol>
	 *   <li>输出层误差：根据预测值与真实值的差异计算</li>
	 *   <li>隐藏层误差：根据后续层的误差反向传播计算</li>
	 * </ol>
	 * 
	 * <p>误差计算公式：</p>
	 * <ul>
	 *   <li>输出层：δ = f'(net) × (target - output)</li>
	 *   <li>隐藏层：δ = f'(net) × Σ(w_jk × δ_k)</li>
	 * </ul>
	 * 
	 * <p>其中f'(net)是激活函数的导数，对于Sigmoid函数：</p>
	 * <p>f'(x) = f(x) × (1 - f(x))</p>
	 * 
	 * <p>计算顺序必须从输出层开始，逐层向前，因为隐藏层的误差
	 * 计算依赖于输出层的误差值。</p>
	 */
	private void backPropagation() {
		// 计算输出层各神经元的误差梯度
		for (int i = 0; i < outputLevel.length; i++) {
			outputLevel[i].setCurrentError(outputLevel[i].calOutputError(currentResult));
		}
		
		// 计算隐藏层各神经元的误差梯度（从输出层反向传播）
		for (int i = 0; i < hiddenLevel.length; i++) {
			hiddenLevel[i].setCurrentError(hiddenLevel[i].calError());
		}
	}

	/**
	 * 改进的权重调整方法，支持动态学习率
	 * 
	 * @param learningRate 当前学习率
	 */
	private void adjust(double learningRate) {
		// 隐藏层权重调整
		for (int i = 0; i < hiddenLevel.length; i++) {
			List<Synapse> synapseList = hiddenLevel[i].getInputSynapseList();
			for (Synapse synapse : synapseList) {
				double weightDelta = synapse.getBackCell().getOutput() * 
					hiddenLevel[i].getCurrentError() * learningRate;
				synapse.setWeight((float)(synapse.getWeight() + weightDelta));
			}
			// 隐藏层偏置调整
			double biasDelta = hiddenLevel[i].getCurrentError() * learningRate;
			hiddenLevel[i].setExcursion((float)(hiddenLevel[i].getExcursion() + biasDelta));
		}

		// 输出层权重调整
		for (int i = 0; i < outputLevel.length; i++) {
			List<Synapse> synapseList = outputLevel[i].getInputSynapseList();
			for (Synapse synapse : synapseList) {
				double weightDelta = synapse.getBackCell().getOutput() * 
					outputLevel[i].getCurrentError() * learningRate;
				synapse.setWeight((float)(synapse.getWeight() + weightDelta));
			}
			// 输出层偏置调整
			double biasDelta = outputLevel[i].getCurrentError() * learningRate;
			outputLevel[i].setExcursion((float)(outputLevel[i].getExcursion() + biasDelta));
		}
	}

	/**
	 * 获取网络的基本信息
	 * 
	 * @return 网络信息字符串
	 */
	public String getNetworkInfo() {
		return String.format(
				"BP神经网络信息:\n" +
				"输入层节点数: %d\n" +
				"隐藏层节点数: %d\n" +
				"输出层节点数: %d\n" +
				"学习率: %.3f\n" +
				"最大迭代次数: %d\n" +
				"输出范围: [%.2f, %.2f]",
				inputLevelCellNum, hiddenLevelCellNum, outputLevelCellNum,
				learnRate, MaxIterateNum, MinRange, MaxRange);
	}

	/**
	 * 计算网络的总参数数量
	 * 
	 * @return 参数数量
	 */
	public int getTotalParameterCount() {
		// 输入层到隐藏层的权重数 + 隐藏层偏置数
		int inputToHiddenWeights = inputLevelCellNum * hiddenLevelCellNum;
		int hiddenBiases = hiddenLevelCellNum;
		
		// 隐藏层到输出层的权重数 + 输出层偏置数
		int hiddenToOutputWeights = hiddenLevelCellNum * outputLevelCellNum;
		int outputBiases = outputLevelCellNum;
		
		return inputToHiddenWeights + hiddenBiases + hiddenToOutputWeights + outputBiases;
	}

	/**
	 * 验证模型在给定数据集上的性能
	 * 
	 * @param testDataSet 测试数据集
	 * @return 平均平方误差(MSE)
	 */
	public <T extends TrainDataItem<Float, Float>> double evaluateModel(Collection<T> testDataSet) {
		if (testDataSet == null || testDataSet.isEmpty()) {
			return Double.NaN;
		}
		
		double totalSquaredError = 0.0;
		int sampleCount = 0;
		
		for (TrainDataItem<Float, Float> item : testDataSet) {
			float predicted = useMode(item.getFeature());
			float actual = item.getResult();
			double error = predicted - actual;
			totalSquaredError += error * error;
			sampleCount++;
		}
		
		return totalSquaredError / sampleCount;
	}

	/**
	 * 改进的使用模型方法，增加输入验证和性能优化
	 * 
	 * @param feature 输入特征数组
	 * @return 预测结果
	 */
	@Override
	public <T extends Number> float useMode(T[] feature) {
		if (feature == null || feature.length == 0) {
			throw new IllegalArgumentException("输入特征不能为空");
		}
		return calUseBpMode((Float[]) feature);
	}

	/**
	 * 改进的BP模型计算方法
	 * 
	 * @param feature 输入特征
	 * @return 预测结果
	 */
	private float calUseBpMode(Float[] feature) {
		if (feature.length != inputLevel.length) {
			throw new IllegalArgumentException(
					"输入特征维度(" + feature.length + ")与输入层节点数(" + 
					inputLevel.length + ")不匹配");
		}

		// 改进输入归一化，保持与训练时一致
		for (int i = 0; i < inputLevel.length; i++) {
			float normalizedInput = (feature[i] - AppContextConstant.AIR_CONDITION_MIN_POWER) / 
				(AppContextConstant.AIR_CONDITION_MAX_POWER - AppContextConstant.AIR_CONDITION_MIN_POWER);
			inputLevel[i].setOutput(normalizedInput);
		}
		
		// 前向传播
		feedForward();

		// 反归一化输出
		return outputLevel[0].getOutput() * (MaxRange - MinRange) + MinRange;
	}

	/**
	 * 神经元细胞体类
	 * 
	 * <p>该类模拟生物神经元的基本功能，包含以下核心组件：</p>
	 * <ul>
	 *   <li>输入突触列表：接收来自前一层神经元的信号</li>
	 *   <li>输出突触列表：向后一层神经元发送信号</li>
	 *   <li>偏置值：神经元的内在激活阈值</li>
	 *   <li>输出值：经过激活函数处理后的输出</li>
	 *   <li>误差值：反向传播中计算的误差梯度</li>
	 * </ul>
	 * 
	 * <p>根据在网络中的位置，神经元分为三种类型：</p>
	 * <ul>
	 *   <li>输入神经元：只有输出连接，直接接收外部数据</li>
	 *   <li>隐藏神经元：既有输入又有输出连接，进行特征提取</li>
	 *   <li>输出神经元：只有输入连接，产生最终预测结果</li>
	 * </ul>
	 * 
	 * @author LeavesFly
	 */
	private static class Cell {
		/** 输入突触列表，存储来自前一层的所有连接 */
		private List<Synapse> inputSynapseList;
		
		/** 输出突触列表，存储到后一层的所有连接 */
		private List<Synapse> outputSynapseList;

		/** 偏置值（阈值），影响神经元的激活难易程度 */
		private float excursion;
		
		/** 当前输出值，经过激活函数处理的结果 */
		private float output;
		
		/** 当前误差值，反向传播中计算的梯度 */
		private float currentError;

		/**
		 * 创建输入层神经元
		 * 
		 * <p>输入层神经元的特点：</p>
		 * <ul>
		 *   <li>偏置值设为0，因为输入层不需要激活函数处理</li>
		 *   <li>没有输入连接，直接接收外部数据</li>
		 *   <li>有输出连接，向隐藏层传递信号</li>
		 * </ul>
		 * 
		 * @return 配置好的输入层神经元实例
		 */
		public static Cell genInputCell() {
			Cell cell = new Cell();
			cell.setExcursion(0f);  // 输入层偏置为0
			cell.setInputSynapseList(Collections.<Synapse> emptyList());  // 无输入连接
			cell.setOutputSynapseList(new ArrayList<Synapse>());  // 有输出连接
			return cell;
		}

		/**
		 * 创建隐藏层神经元
		 * 
		 * <p>隐藏层神经元的特点：</p>
		 * <ul>
		 *   <li>偏置值随机初始化，用于打破对称性</li>
		 *   <li>有输入连接，接收来自输入层的信号</li>
		 *   <li>有输出连接，向输出层传递处理后的信号</li>
		 *   <li>使用Sigmoid激活函数进行非线性变换</li>
		 * </ul>
		 * 
		 * @param excursion 偏置值，通常在[-0.5, 0.5]范围内随机初始化
		 * @return 配置好的隐藏层神经元实例
		 */
		public static Cell genHiddenCell(float excursion) {
			Cell cell = new Cell();
			cell.setExcursion(excursion);  // 设置随机偏置值
			cell.setInputSynapseList(new ArrayList<Synapse>());  // 有输入连接
			cell.setOutputSynapseList(new ArrayList<Synapse>());  // 有输出连接
			return cell;
		}

		/**
		 * 创建输出层神经元
		 * 
		 * <p>输出层神经元的特点：</p>
		 * <ul>
		 *   <li>偏置值随机初始化，影响最终输出的偏移</li>
		 *   <li>有输入连接，接收来自隐藏层的信号</li>
		 *   <li>没有输出连接，产生网络的最终预测结果</li>
		 *   <li>使用Sigmoid激活函数，输出值在[0,1]范围内</li>
		 * </ul>
		 * 
		 * @param excursion 偏置值，通常在[-0.5, 0.5]范围内随机初始化
		 * @return 配置好的输出层神经元实例
		 */
		public static Cell genOutputCell(float excursion) {
			Cell cell = new Cell();
			cell.setExcursion(excursion);  // 设置随机偏置值
			cell.setInputSynapseList(new ArrayList<Synapse>());  // 有输入连接
			cell.setOutputSynapseList(Collections.<Synapse> emptyList());  // 无输出连接
			return cell;
		}

		/**
		 * 计算神经元输出值（前向传播）
		 * 使用Sigmoid激活函数
		 * 
		 * @return 激活后的输出值
		 */
		public float calOutput() {
			float weightedSum = 0f;
			// 计算加权输入总和
			for (Synapse backSynapse : inputSynapseList) {
				weightedSum += backSynapse.getBackCell().getOutput() * backSynapse.getWeight();
			}
			// 加上偏置项
			weightedSum += excursion;
			
			// 应用Sigmoid激活函数：f(x) = 1/(1+e^(-x))
			// 为防止数值溢出，限制输入范围
			if (weightedSum > 500) weightedSum = 500;
			else if (weightedSum < -500) weightedSum = -500;
			
			return (float) (1.0 / (1.0 + Math.exp(-weightedSum)));
		}

		/**
		 * 计算隐藏层神经元的误差梯度（反向传播）
		 * 
		 * @return 误差梯度值
		 */
		public float calError() {
			float errorSum = 0f;
			// 累积来自后续层的误差信号
			for (Synapse frontSynapse : outputSynapseList) {
				errorSum += frontSynapse.getFrontCell().getCurrentError() * frontSynapse.getWeight();
			}
			// 应用Sigmoid导数：f'(x) = f(x) * (1 - f(x))
			return output * (1.0f - output) * errorSum;
		}

		/**
		 * 计算输出层神经元的误差梯度
		 * 
		 * @param targetResult 目标输出值（已归一化）
		 * @return 误差梯度值
		 */
		public float calOutputError(float targetResult) {
			// 输出层误差：δ = f'(x) * (target - actual)
			// 其中 f'(x) = f(x) * (1 - f(x)) 是Sigmoid导数
			return output * (1.0f - output) * (targetResult - output);
		}

		// ================== Getter 和 Setter 方法 ==================
		
		/**
		 * 获取输入突触列表
		 * 
		 * @return 输入突触列表，不可修改
		 */
		public List<Synapse> getInputSynapseList() {
			return inputSynapseList;
		}

		/**
		 * 设置输入突触列表（内部使用）
		 * 
		 * @param inputSynapseList 输入突触列表
		 */
		private void setInputSynapseList(List<Synapse> inputSynapseList) {
			this.inputSynapseList = inputSynapseList;
		}

		/**
		 * 获取输出突触列表
		 * 
		 * @return 输出突触列表，不可修改
		 */
		public List<Synapse> getOutputSynapseList() {
			return outputSynapseList;
		}

		/**
		 * 设置输出突触列表（内部使用）
		 * 
		 * @param outPutSynapseList 输出突触列表
		 */
		private void setOutputSynapseList(List<Synapse> outPutSynapseList) {
			this.outputSynapseList = outPutSynapseList;
		}

		/**
		 * 获取偏置值
		 * 
		 * @return 当前神经元的偏置值
		 */
		public float getExcursion() {
			return excursion;
		}

		/**
		 * 设置偏置值
		 * 
		 * <p>偏置值在训练过程中会根据梯度下降算法进行调整。</p>
		 * 
		 * @param excursion 新的偏置值
		 */
		public void setExcursion(float excursion) {
			this.excursion = excursion;
		}

		/**
		 * 设置神经元输出值
		 * 
		 * <p>输出值通常由calOutput()方法计算得出，或者在输入层直接设置。</p>
		 * 
		 * @param output 输出值，对于Sigmoid激活函数，范围为[0,1]
		 */
		public void setOutput(float output) {
			this.output = output;
		}

		/**
		 * 获取神经元输出值
		 * 
		 * @return 当前神经元的输出值
		 */
		public float getOutput() {
			return output;
		}

		/**
		 * 获取当前误差值
		 * 
		 * @return 反向传播中计算的误差梯度
		 */
		public float getCurrentError() {
			return currentError;
		}

		/**
		 * 设置当前误差值
		 * 
		 * <p>误差值在反向传播过程中计算，用于权重和偏置的更新。</p>
		 * 
		 * @param currentError 误差梯度值
		 */
		public void setCurrentError(float currentError) {
			this.currentError = currentError;
		}

	}

	/**
	 * 神经网络突触类
	 * 
	 * <p>突触模拟生物神经系统中神经元之间的连接，承载以下功能：</p>
	 * <ul>
	 *   <li>信号传递：将前一层神经元的输出信号传递给后一层</li>
	 *   <li>权重调制：通过权重值控制信号的强度</li>
	 *   <li>学习记忆：在训练过程中调整权重存储经验</li>
	 * </ul>
	 * 
	 * <p>每个突触连接两个神经元：</p>
	 * <ul>
	 *   <li>backCell：信号来源神经元（前一层）</li>
	 *   <li>frontCell：信号目标神经元（后一层）</li>
	 * </ul>
	 * 
	 * <p>权重值的意义：</p>
	 * <ul>
	 *   <li>正权重：信号增强，前一层激活促进后一层激活</li>
	 *   <li>负权重：信号抑制，前一层激活抑制后一层激活</li>
	 *   <li>零权重：无信号传递，相当于连接断开</li>
	 * </ul>
	 * 
	 * @author LeavesFly
	 */
	private static class Synapse {
		// 突触连接的方向：从 backCell 到 frontCell
		/** 信号来源神经元（前一层神经元） */
		private Cell backCell;
		
		/** 信号目标神经元（后一层神经元） */
		private Cell frontCell;
		
		/** 连接权重，控制信号传递的强度和方向 */
		private float weight;

		/**
		 * 突触构造函数
		 * 
		 * <p>创建一个连接两个神经元的突触，并设置初始权重。</p>
		 * 
		 * @param backCell 信号来源神经元，不能为null
		 * @param frontCell 信号目标神经元，不能为null
		 * @param weight 初始权重值，通常在[-1.0, 1.0]范围内随机初始化
		 */
		public Synapse(Cell backCell, Cell frontCell, float weight) {
			this.backCell = backCell;
			this.frontCell = frontCell;
			this.weight = weight;
		}

		/**
		 * 获取信号来源神经元
		 * 
		 * @return 前一层神经元，信号的来源
		 */
		public Cell getBackCell() {
			return backCell;
		}

		/**
		 * 获取信号目标神经元
		 * 
		 * @return 后一层神经元，信号的目标
		 */
		public Cell getFrontCell() {
			return frontCell;
		}

		/**
		 * 获取当前权重值
		 * 
		 * @return 突触的权重值
		 */
		public float getWeight() {
			return weight;
		}

		/**
		 * 设置权重值
		 * 
		 * <p>该方法在训练过程中被调用，根据梯度下降算法更新权重。</p>
		 * 
		 * @param weight 新的权重值
		 */
		public void setWeight(float weight) {
			this.weight = weight;
		}

	}

	/**
	 * 测试主方法，演示如何使用BP神经网络模型
	 * 
	 * <p>该方法展示了完整的模型使用流程：</p>
	 * <ol>
	 *   <li>数据收集：从文件加载训练数据</li>
	 *   <li>数据管理：存储和管理训练数据集</li>
	 *   <li>数据筛选：按传感器ID和温度类型筛选数据</li>
	 *   <li>模型创建：创建并初始化BP神经网络</li>
	 *   <li>模型训练：使用训练数据训练模型</li>
	 *   <li>模型验证：使用相同数据验证模型效果</li>
	 * </ol>
	 * 
	 * <p>注意：这里使用相同数据进行训练和验证只是为了演示，
	 * 实际应用中应该将数据分为训练集、验证集和测试集。</p>
	 * 
	 * @param args 命令行参数（未使用）
	 */
	public static void main(String[] args) {
		// 创建数据收集器，指定训练数据文件
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);

		// 从文本文件中收集训练数据
		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();

		// 获取训练数据集管理器
		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();

		// 将收集到的数据存储到管理器中
		trainDataSetManager.storeTrainDataSet(trainDatas);

		// 根据传感器ID和温度类型获取特定的训练数据子集
		Collection<IntellacTrainDataItem> datas = trainDataSetManager.fetchTrainDataSetBySensorId(
				"1", AppContextConstant.OUTSIDE_TEMP);
		
		// 创建和初始化BP神经网络模型
		TrainModel trainModel = BpnnModel.getIntance();
		
		// 使用筛选后的数据训练模型
		trainModel.train(datas);

		// 验证模型效果：对每个训练样本进行预测并输出结果
		for (IntellacTrainDataItem item : datas) {
			System.out.println("训练样本: " + item);
			System.out.println("预测结果: " + trainModel.useMode(item.getFeature()));
			System.out.println("-------------------");
		}
	}

}
