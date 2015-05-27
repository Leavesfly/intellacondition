package com.leavesfly.iac.evalute;

import com.leavesfly.iac.config.AppContextConstant;

public class EvaluteResult {

	private Solution solution;
	private float totalSatisfaction;
	private float totalPowerCost;
	private float powerUtility;

	public EvaluteResult(Solution solution, float totalSatisfaction, float totalPowerCost) {
		this.solution = solution;
		this.totalSatisfaction = totalSatisfaction;
		this.totalPowerCost = totalPowerCost;
		powerUtility = (totalSatisfaction / totalPowerCost) * AppContextConstant.POWER_UTILITY_UNIT;

	}

	public Solution getSolution() {
		return solution;
	}

	public String getSolutionName() {
		return solution.getSolutionName();
	}

	public float getTotalSatisfaction() {
		return totalSatisfaction;
	}

	public float getTotalPowerCost() {
		return totalPowerCost;
	}

	public float getPowerUtility() {
		return powerUtility;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(solution.toString()).append("\t").append("totalSatisfaction:")
				.append(totalSatisfaction).append("\t").append("totalPowerCost:")
				.append(totalPowerCost).append("\t").append("powerUtility:").append(powerUtility);
		return strBuilder.toString();
	}
}
