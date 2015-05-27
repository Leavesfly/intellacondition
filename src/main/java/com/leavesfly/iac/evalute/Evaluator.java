package com.leavesfly.iac.evalute;

import java.util.Collection;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.execute.domain.UserComfortFunc;

public class Evaluator {

	/**
	 * 根据当前的功率向量计算用户总的满意度
	 * 
	 * @param powerVector
	 * @return
	 */
	public static float calTotalSatisfaction(PowerVector powerVector) {

		if (powerVector == null) {
			throw new IllegalArgumentException();
		}
		float satisfaction = 0f;
		PowerValue[] powerValueArray = powerVector.getPowerValueVector();

		Collection<UserComfortFunc> userComfortFuncSet = DataFactory.getInstance()
				.getUserComfortFuncCollection();
		for (UserComfortFunc userComfortFunc : userComfortFuncSet) {

			String userId = userComfortFunc.getUserId();
			Collection<PtFitFunc> sensorFuncSet = getAroundFitFunc(userId);
			float temperature = calUserPointTemp(sensorFuncSet, powerValueArray);
			satisfaction += userComfortFunc.calUserComfort(temperature);
		}
		return satisfaction;
	}

	private static Collection<PtFitFunc> getAroundFitFunc(String userId) {
		DataFactory dataFactory = DataFactory.getInstance();
		Collection<PtFitFunc> sensorFuncSet = dataFactory.getSensorFuncByUserId().get(userId);
		if (sensorFuncSet == null || sensorFuncSet.isEmpty()) {
			return dataFactory.getSensorFitFuncSet();
		}

		return sensorFuncSet;
	}

	private static float calUserPointTemp(Collection<PtFitFunc> sensorFuncSet, PowerValue[] location) {
		float result = 0.0f;
		for (PtFitFunc fitFunc : sensorFuncSet) {
			result += fitFunc.calTemperature(new PowerVector(location));
		}
		return result / sensorFuncSet.size();
	}

	/**
	 * 根据当前的功率向量计算总的用电量
	 * 
	 * @param powerVector
	 * @return
	 */
	public static float calTotalPowerCost(PowerVector powerVector) {
		if (powerVector == null) {
			throw new IllegalArgumentException();
		}
		float powerCost = 0.0f;
		PowerValue[] powerValueArray = powerVector.getPowerValueVector();
		for (PowerValue powerValue : powerValueArray) {
			powerCost += powerValue.getValue() * AppContextConstant.POWER_PRICE;
		}

		return powerCost;
	}

	/**
	 * 
	 * @param solution
	 * @return
	 */
	public static EvaluteResult transform(Solution solution) {

		PowerVector powerVector = solution.getPowerVector();
		float satisfaction = calTotalSatisfaction(powerVector);
		float powerCost = calTotalPowerCost(powerVector);
		return new EvaluteResult(solution, satisfaction, powerCost);

	}

}
