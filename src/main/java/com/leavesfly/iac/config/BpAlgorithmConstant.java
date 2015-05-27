package com.leavesfly.iac.config;

public class BpAlgorithmConstant {
	public static final int INPUT_LEVEL_CELL_NUM = AppContextConstant.AIR_CONDITION_NUM;
	public static final int OUTPUT_LEVEL_CELL_NUM = 1;
	public static final int HIDDEN_LEVEL_CELL_NUM = determineHiddenLevel(INPUT_LEVEL_CELL_NUM,
			OUTPUT_LEVEL_CELL_NUM);

	public static final float LEARN_RATE = 0.6f;
	public static final int ITERATE_NUM = 1_000;

	public static final float EXCURSION_RANDOM_FROM = -0.5f;
	public static final float EXCURSION_RANDOM_TO = 0.5f;

	public static final float WEIGHT_RANDOM_FROM = -1.0f;
	public static final float WEIGHT_RANDOM_TO = 1.0f;
	
	public static final float ADJUST_FACTOR = 0.3f;

	public static final float MIN_ERROR = 0.000_001f;
	public static final float MAX_RESULT_NORMAL = AppContextConstant.AIR_CONDITION_MAX_TEMP;
	public static final float MIN_RESULT_NORMAL = AppContextConstant.AIR_CONDITION_MIN_TEMP;

	public static int determineHiddenLevel(int inputLevelNum, int outputLevelNum) {
		long hiddenLevelNum = 0;
		hiddenLevelNum = Math.round(Math.sqrt(0.43f * Math.pow(inputLevelNum, 2.0f) + 0.12f
				* Math.pow(outputLevelNum, 2.0f) + 2.54f * inputLevelNum + 0.77f * outputLevelNum
				+ 0.35f) + 0.51f);
		return (int) hiddenLevelNum;
	}

	public static void main(String[] args) {
		System.out.println(HIDDEN_LEVEL_CELL_NUM);
	}

}
