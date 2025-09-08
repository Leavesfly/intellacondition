package com.leavesfly.iac.config;

/**
 * BP神经网络算法常量配置类
 * 
 * 该类定义了BP神经网络算法的各种超参数和配置常量，包括：
 * 1. 网络结构参数（输入层、输出层、隐藏层节点数）
 * 2. 学习参数（学习率、迭代次数等）
 * 3. 权重和偏移量初始化范围
 * 4. 训练停止条件
 */
public class BpAlgorithmConstant {
	/**
	 * 输入层节点数，等于空调数量
	 */
	public static final int INPUT_LEVEL_CELL_NUM = AppContextConstant.AIR_CONDITION_NUM;
	
	/**
	 * 输出层节点数
	 */
	public static final int OUTPUT_LEVEL_CELL_NUM = 1;
	
	/**
	 * 隐藏层节点数，通过公式计算得出
	 */
	public static final int HIDDEN_LEVEL_CELL_NUM = determineHiddenLevel(INPUT_LEVEL_CELL_NUM,
			OUTPUT_LEVEL_CELL_NUM);

	/**
	 * 学习率
	 */
	public static final float LEARN_RATE = 0.6f;
	
	/**
	 * 迭代次数
	 */
	public static final int ITERATE_NUM = 1_000;

	/**
	 * 偏移量随机初始化范围起始值
	 */
	public static final float EXCURSION_RANDOM_FROM = -0.5f;
	
	/**
	 * 偏移量随机初始化范围结束值
	 */
	public static final float EXCURSION_RANDOM_TO = 0.5f;

	/**
	 * 权重随机初始化范围起始值
	 */
	public static final float WEIGHT_RANDOM_FROM = -1.0f;
	
	/**
	 * 权重随机初始化范围结束值
	 */
	public static final float WEIGHT_RANDOM_TO = 1.0f;
	
	/**
	 * 调整因子
	 */
	public static final float ADJUST_FACTOR = 0.3f;

	/**
	 * 最小误差阈值
	 */
	public static final float MIN_ERROR = 0.000_001f;
	
	/**
	 * 最大结果归一化值，等于空调最高温度
	 */
	public static final float MAX_RESULT_NORMAL = AppContextConstant.AIR_CONDITION_MAX_TEMP;
	
	/**
	 * 最小结果归一化值，等于空调最低温度
	 */
	public static final float MIN_RESULT_NORMAL = AppContextConstant.AIR_CONDITION_MIN_TEMP;

	/**
	 * 确定隐藏层节点数
	 * 
	 * 使用经验公式计算隐藏层节点数：
	 * hidden = sqrt(0.43*input^2 + 0.12*output^2 + 2.54*input + 0.77*output + 0.35) + 0.51
	 * 
	 * @param inputLevelNum 输入层节点数
	 * @param outputLevelNum 输出层节点数
	 * @return 计算得出的隐藏层节点数
	 */
	public static int determineHiddenLevel(int inputLevelNum, int outputLevelNum) {
		long hiddenLevelNum = 0;
		hiddenLevelNum = Math.round(Math.sqrt(0.43f * Math.pow(inputLevelNum, 2.0f) + 0.12f
				* Math.pow(outputLevelNum, 2.0f) + 2.54f * inputLevelNum + 0.77f * outputLevelNum
				+ 0.35f) + 0.51f);
		return (int) hiddenLevelNum;
	}

	/**
	 * 主函数，用于测试隐藏层节点数
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		System.out.println(HIDDEN_LEVEL_CELL_NUM);
	}

}