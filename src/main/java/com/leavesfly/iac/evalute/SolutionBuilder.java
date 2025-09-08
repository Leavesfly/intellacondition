package com.leavesfly.iac.evalute;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.datagene.AirConditionUtil;
import com.leavesfly.iac.domain.PowerVector;

/**
 * 解决方案构建器类
 * 
 * 该类提供了静态方法用于构建不同类型的空调功率调度解决方案。
 */
public class SolutionBuilder {

	/**
	 * 根据统一温度值构建解决方案
	 * 
	 * 该方法根据指定的统一温度值，计算每个空调设备对应的功率值，
	 * 并构建包含这些功率值的解决方案。
	 * 
	 * @param solutionName 解决方案名称
	 * @param sameTemp 统一温度值
	 * @return 构建的解决方案
	 */
	public static Solution buildSolution(String solutionName, float sameTemp) {

		Float[] powerValueArray = new Float[AppContextConstant.AIR_CONDITION_NUM];
		for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
			powerValueArray[i] = AirConditionUtil.costPowerWhenTemp(sameTemp);
		}
		PowerVector powerVector = new PowerVector(powerValueArray,
				AppContextConstant.AIR_CONDITION_NUM);

		return new Solution(solutionName, powerVector);
	}

	/**
	 * 根据功率向量构建解决方案
	 * 
	 * @param solutionName 解决方案名称
	 * @param powerVector 功率向量
	 * @return 构建的解决方案
	 */
	public static Solution buildSolution(String solutionName, PowerVector powerVector) {
		return new Solution(solutionName, powerVector);
	}
}