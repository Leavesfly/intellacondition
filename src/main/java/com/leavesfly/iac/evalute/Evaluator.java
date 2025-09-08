package com.leavesfly.iac.evalute;

import java.util.Collection;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.execute.domain.UserComfortFunc;

/**
 * 评估器类
 * 
 * 该类负责对空调功率调度方案进行评估，计算用户满意度和用电成本等指标。
 */
public class Evaluator {

	/**
	 * 根据当前的功率向量计算用户总的满意度
	 * 
	 * 该方法通过以下步骤计算总满意度：
	 * 1. 获取所有用户的舒适度函数
	 * 2. 对每个用户，计算其周围传感器的温度预测值
	 * 3. 根据预测温度和用户舒适度函数计算满意度
	 * 4. 累加所有用户的满意度得到总满意度
	 * 
	 * @param powerVector 功率向量
	 * @return 总满意度
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

	/**
	 * 获取用户周围的温度预测函数集合
	 * 
	 * @param userId 用户ID
	 * @return 用户周围的温度预测函数集合
	 */
	private static Collection<PtFitFunc> getAroundFitFunc(String userId) {
		DataFactory dataFactory = DataFactory.getInstance();
		Collection<PtFitFunc> sensorFuncSet = dataFactory.getSensorFuncByUserId().get(userId);
		if (sensorFuncSet == null || sensorFuncSet.isEmpty()) {
			return dataFactory.getSensorFitFuncSet();
		}

		return sensorFuncSet;
	}

	/**
	 * 计算用户位置的温度
	 * 
	 * 通过平均用户周围所有传感器的温度预测值得到用户位置的温度
	 * 
	 * @param sensorFuncSet 传感器温度预测函数集合
	 * @param location 位置功率值数组
	 * @return 用户位置的温度
	 */
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
	 * 该方法计算所有空调设备功率值的总和，并乘以电价得到总用电成本
	 * 
	 * @param powerVector 功率向量
	 * @return 总用电成本
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
	 * 转换解决方案为评估结果
	 * 
	 * 该方法对给定的解决方案进行评估，计算满意度和用电成本，
	 * 并生成对应的评估结果对象
	 * 
	 * @param solution 解决方案
	 * @return 评估结果
	 */
	public static EvaluteResult transform(Solution solution) {

		PowerVector powerVector = solution.getPowerVector();
		float satisfaction = calTotalSatisfaction(powerVector);
		float powerCost = calTotalPowerCost(powerVector);
		return new EvaluteResult(solution, satisfaction, powerCost);

	}

}