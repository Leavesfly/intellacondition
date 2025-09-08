package com.leavesfly.iac.execute.scheduler.pso;

import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.util.ArrayCloneUtil;

/**
 * 粒子群优化算法类
 * 
 * 该类实现了标准的粒子群优化算法，用于寻找最优解。
 * 算法通过迭代更新粒子群中每个粒子的位置和速度，
 * 并跟踪全局最优解。
 * 
 * @param <T> 粒子位置和速度的类型，必须实现RangeValue接口
 */
public class PsoAlgorithm<T extends RangeValue> {

	/**
	 * 粒子群数组
	 */
	private Particle<T>[] particleSet;
	
	/**
	 * 全局最优位置
	 */
	private T[] globalBestLocation;
	
	/**
	 * 全局最优值
	 */
	private float globalBestValue;

	/**
	 * 构造函数
	 * 
	 * @param particleSet 粒子群数组
	 */
	public PsoAlgorithm(Particle<T>[] particleSet) {
		this.particleSet = particleSet;
		initBestGlobalBestLocationAndValue();
	}

	/**
	 * 初始化全局最优位置和值
	 * 
	 * 从粒子群中找出初始的全局最优位置和值
	 */
	private void initBestGlobalBestLocationAndValue() {

		Particle<T> bestParticle = getBestLocation();
		globalBestLocation = ArrayCloneUtil.arrayDeepCopy(bestParticle.getBestLocation());
		globalBestValue = bestParticle.getBestTargetValue();
	}

	/**
	 * 获取最优粒子
	 * 
	 * 遍历粒子群，找出历史最优目标值最大的粒子
	 * 
	 * @return 最优粒子
	 */
	private Particle<T> getBestLocation() {
		Particle<T> bestParticle = null;
		float bestValue = Float.MIN_VALUE;
		for (Particle<T> particle : particleSet) {
			if (bestValue < particle.getBestTargetValue()) {
				bestParticle = particle;
				bestValue = particle.getBestTargetValue();
			}
		}
		return bestParticle;
	}

	/**
	 * 寻找最优位置
	 * 
	 * 通过指定次数的迭代优化，寻找全局最优位置
	 * 
	 * @param iterateNum 迭代次数
	 * @return 全局最优位置
	 */
	public T[] findBestLocation(int iterateNum) {
		while (iterateNum > 0) {
			for (Particle<T> particle : particleSet) {
				particle.updateParticle(globalBestLocation);
			}
			setBestLocationAndValue();
			iterateNum--;
		}
		return globalBestLocation;
	}

	/**
	 * 更新全局最优位置和值
	 * 
	 * 在每次迭代后，检查是否有粒子找到了更好的解，
	 * 如果有则更新全局最优位置和值
	 */
	private void setBestLocationAndValue() {
		Particle<T> bestParticle = getBestLocation();
		globalBestValue = bestParticle.getBestTargetValue();

		T[] bestLocation = bestParticle.getBestLocation();
		for (int i = 0; i < bestLocation.length; i++) {
			globalBestLocation[i].setValue(bestLocation[i].getValue());
		}
	}

	/**
	 * 获取全局最优值
	 * 
	 * @return 全局最优值
	 */
	public float getGlobalBestValue() {
		return globalBestValue;
	}
}