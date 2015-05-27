package com.leavesfly.iac.execute;

import com.leavesfly.iac.domain.PowerVector;

/**
 * 抽象的功率调度器
 * 
 * @author LeavesFly
 *
 */
public interface PowerScheduler {

	public PowerVector schedule();
}
