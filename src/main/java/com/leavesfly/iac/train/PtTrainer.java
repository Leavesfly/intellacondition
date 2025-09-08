package com.leavesfly.iac.train;

import java.util.Collection;
import com.leavesfly.iac.domain.PtFitFunc;

/**
 * 功率-温度训练器接口
 * 
 * 该接口定义了功率-温度模型训练器的基本功能，
 * 用于在指定室外温度下为每个传感器节点训练功率-温度拟合函数。
 */
public interface PtTrainer {
	/**
	 * 在指定室外温度下，训练每个传感器节点的功率-温度拟合函数集合
	 * 
	 * @param outsideTemp 室外温度
	 * @return 功率-温度拟合函数集合
	 */
	public Collection<PtFitFunc> buildFitFuncSet(float outsideTemp);
}