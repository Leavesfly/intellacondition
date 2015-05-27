package com.leavesfly.iac.train.trainer;

/**
 * 训练模型的枚举类
 * 
 * @author yefei.yf
 *
 */
public enum ModelEnum {

	BPNN("Back Propagation Neural Network"), BPWEKA("BP Neural Network of Weka"), LR(
			"Linear Regression ");

	private String name;

	private ModelEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
