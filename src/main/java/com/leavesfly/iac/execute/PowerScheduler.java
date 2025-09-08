package com.leavesfly.iac.execute;

import com.leavesfly.iac.domain.PowerVector;

/**
 * 功率调度器接口
 * 
 * 该接口定义了功率调度器的基本功能，用于执行空调功率调度算法，
 * 并返回最优的功率向量配置。
 */
public interface PowerScheduler {

	/**
	 * 执行功率调度算法
	 * 
	 * @return 调度后的功率向量
	 */
	public PowerVector schedule();
}