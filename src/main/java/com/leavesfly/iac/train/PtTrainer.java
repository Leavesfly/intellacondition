package com.leavesfly.iac.train;

import java.util.Collection;
import com.leavesfly.iac.domain.PtFitFunc;

public interface PtTrainer {
	/**
	 * 外部温度为outsideTemp时，训练每个传感节点的的拟合函数的集合
	 * 
	 * @param outsideTemp
	 * @return
	 */
	public Collection<PtFitFunc> buildFitFuncSet(float outsideTemp);
}
