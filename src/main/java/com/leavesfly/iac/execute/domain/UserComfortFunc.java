package com.leavesfly.iac.execute.domain;

import com.leavesfly.iac.config.AppContextConstant;

public abstract class UserComfortFunc {

	protected static final float COMFORT_MIN_VALUE = AppContextConstant.COMFORT_MIN_VALUE;
	protected String userId;

	protected UserComfortFunc(String userId) {
		this.userId = userId;
	}

	/**
	 * 
	 * @param temperature
	 * @return
	 */
	public abstract float calUserComfort(float temperature);

	/**
	 * 
	 * @param temperature
	 * @return
	 */
	//public abstract float calUserComfortExt(float temperature);

	/**
	 * 
	 * @param temperature
	 * @return
	 */
	public abstract boolean isUpMinSatisfy(float temperature);

	public String getUserId() {
		return userId;

	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
