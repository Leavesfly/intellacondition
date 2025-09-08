package com.leavesfly.iac.evalute;

import com.leavesfly.iac.domain.PowerVector;

/**
 * 解决方案类
 * 
 * 该类表示一个空调功率调度解决方案，包含解决方案名称和对应的功率向量。
 */
public class Solution {
	/**
	 * 解决方案名称
	 */
	private String solutionName;
	
	/**
	 * 功率向量
	 */
	private PowerVector powerVector;

	/**
	 * 构造函数
	 * 
	 * @param solutionName 解决方案名称
	 * @param powerVector 功率向量
	 */
	public Solution(String solutionName, PowerVector powerVector) {
		this.solutionName = solutionName;
		this.powerVector = powerVector;
	}

	/**
	 * 获取解决方案名称
	 * 
	 * @return 解决方案名称
	 */
	public String getSolutionName() {
		return solutionName;
	}

	/**
	 * 获取功率向量
	 * 
	 * @return 功率向量
	 */
	public PowerVector getPowerVector() {
		return powerVector;
	}

	/**
	 * 转换为字符串表示
	 * 
	 * @return 字符串表示
	 */
	@Override
	public String toString() {
		return solutionName + ":" + powerVector.toString();
	}
}