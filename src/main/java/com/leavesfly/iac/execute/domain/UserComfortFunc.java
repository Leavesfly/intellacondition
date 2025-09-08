package com.leavesfly.iac.execute.domain;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 用户舒适度函数抽象类
 * 
 * 该类定义了用户舒适度函数的基本结构和接口，用于计算用户在特定温度下的舒适度。
 * 不同类型的用户舒适度函数可以继承该类并实现具体的计算方法。
 */
public abstract class UserComfortFunc {

	/**
	 * 舒适度最小值常量
	 */
	protected static final float COMFORT_MIN_VALUE = AppContextConstant.COMFORT_MIN_VALUE;
	
	/**
	 * 用户ID
	 */
	protected String userId;

	/**
	 * 构造函数
	 * 
	 * @param userId 用户ID
	 */
	protected UserComfortFunc(String userId) {
		this.userId = userId;
	}

	/**
	 * 
	 * @param temperature
	 * @return
	 */
	/**
	 * 计算用户舒适度
	 * 
	 * @param temperature 温度值
	 * @return 用户在该温度下的舒适度
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
	/**
	 * 判断是否满足最低舒适度要求
	 * 
	 * @param temperature 温度值
	 * @return 如果满足最低舒适度要求返回true，否则返回false
	 */
	public abstract boolean isUpMinSatisfy(float temperature);

	/**
	 * 获取用户ID
	 * 
	 * @return 用户ID
	 */
	public String getUserId() {
		return userId;

	}

	/**
	 * 设置用户ID
	 * 
	 * @param userId 用户ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
