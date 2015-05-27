package com.leavesfly.iac.display;

import java.util.Collection;

import com.leavesfly.iac.evalute.EvaluteResult;

public interface ResultDisplayer {
	/**
	 * 
	 * @param evaluteResultSet
	 */
	public void diplayResult(Collection<EvaluteResult> evaluteResultSet);
}
