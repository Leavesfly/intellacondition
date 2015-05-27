package com.leavesfly.iac.train.domain;

/**
 * 训练数据的抽象
 * 
 * @author yefei.yf
 *
 * @param <F>
 * @param <R>
 */
public interface TrainDataItem<F, R> {

	public F[] getFeature();

	public R getResult();

}
