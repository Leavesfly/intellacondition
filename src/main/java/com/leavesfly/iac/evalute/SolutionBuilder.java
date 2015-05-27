package com.leavesfly.iac.evalute;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.datagene.AirConditionUtil;
import com.leavesfly.iac.domain.PowerVector;

public class SolutionBuilder {

	public static Solution buildSolution(String solutionName, float sameTemp) {

		Float[] powerValueArray = new Float[AppContextConstant.AIR_CONDITION_NUM];
		for (int i = 0; i < AppContextConstant.AIR_CONDITION_NUM; i++) {
			powerValueArray[i] = AirConditionUtil.costPowerWhenTemp(sameTemp);
		}
		PowerVector powerVector = new PowerVector(powerValueArray,
				AppContextConstant.AIR_CONDITION_NUM);

		return new Solution(solutionName, powerVector);
	}

	public static Solution buildSolution(String solutionName, PowerVector powerVector) {
		return new Solution(solutionName, powerVector);
	}
}
