package com.leavesfly.iac.train.trainer;

/**
 * 训练模型枚举类
 * 
 * 该枚举类定义了系统支持的训练模型类型，包括BP神经网络、Weka的BP神经网络和线性回归模型。
 */
public enum ModelEnum {

	/**
	 * 反向传播神经网络
	 */
	BPNN("Back Propagation Neural Network"), 
	
	/**
	 * Weka的BP神经网络
	 */
	BPWEKA("BP Neural Network of Weka"), 
	
	/**
	 * 线性回归模型
	 */
	LR("Linear Regression ");

	/**
	 * 模型名称
	 */
	private String name;

	/**
	 * 构造函数
	 * 
	 * @param name 模型名称
	 */
	private ModelEnum(String name) {
		this.name = name;
	}

	/**
	 * 获取模型名称
	 * 
	 * @return 模型名称
	 */
	public String getName() {
		return name;
	}
}