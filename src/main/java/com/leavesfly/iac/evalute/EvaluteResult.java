package com.leavesfly.iac.evalute;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 评估结果类
 * 
 * 该类表示对某个空调功率调度方案的评估结果，包含满意度、用电成本和功率效用等指标。
 */
public class EvaluteResult {

	/**
	 * 解决方案
	 */
	private Solution solution;
	
	/**
	 * 总满意度
	 */
	private float totalSatisfaction;
	
	/**
	 * 总用电成本
	 */
	private float totalPowerCost;
	
	/**
	 * 功率效用
	 */
	private float powerUtility;

	/**
	 * 构造函数
	 * 
	 * @param solution 解决方案
	 * @param totalSatisfaction 总满意度
	 * @param totalPowerCost 总用电成本
	 */
	public EvaluteResult(Solution solution, float totalSatisfaction, float totalPowerCost) {
		this.solution = solution;
		this.totalSatisfaction = totalSatisfaction;
		this.totalPowerCost = totalPowerCost;
		powerUtility = (totalSatisfaction / totalPowerCost) * AppContextConstant.POWER_UTILITY_UNIT;

	}

	/**
	 * 获取解决方案
	 * 
	 * @return 解决方案
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * 获取解决方案名称
	 * 
	 * @return 解决方案名称
	 */
	public String getSolutionName() {
		return solution.getSolutionName();
	}

	/**
	 * 获取总满意度
	 * 
	 * @return 总满意度
	 */
	public float getTotalSatisfaction() {
		return totalSatisfaction;
	}

	/**
	 * 获取总用电成本
	 * 
	 * @return 总用电成本
	 */
	public float getTotalPowerCost() {
		return totalPowerCost;
	}

	/**
	 * 获取功率效用
	 * 
	 * @return 功率效用
	 */
	public float getPowerUtility() {
		return powerUtility;
	}

	/**
	 * 转换为字符串表示
	 * 
	 * @return 字符串表示
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(solution.toString()).append("\t").append("totalSatisfaction:")
				.append(totalSatisfaction).append("\t").append("totalPowerCost:")
				.append(totalPowerCost).append("\t").append("powerUtility:").append(powerUtility);
		return strBuilder.toString();
	}
}