package com.leavesfly.iac.evalute;

import com.leavesfly.iac.domain.PowerVector;

public class Solution {
	private String solutionName;
	private PowerVector powerVector;

	public Solution(String solutionName, PowerVector powerVector) {
		this.solutionName = solutionName;
		this.powerVector = powerVector;
	}

	public String getSolutionName() {
		return solutionName;
	}

	public PowerVector getPowerVector() {
		return powerVector;
	}

	@Override
	public String toString() {
		return solutionName + ":" + powerVector.toString();
	}
}
