package com.leavesfly.iac.execute.scheduler;

import com.leavesfly.iac.config.PsoAlgorithmConstant;
import com.leavesfly.iac.datasource.DataFactory;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.execute.PowerScheduler;
import com.leavesfly.iac.execute.scheduler.pso.Particle;
import com.leavesfly.iac.execute.scheduler.pso.PsoAlgorithm;

/**
 * PSO功率调度器类
 * 
 * 该类实现了基于粒子群优化算法的空调功率调度器，
 * 可以选择使用标准粒子或改进粒子进行优化计算。
 */
public class PsoPowerScheduler implements PowerScheduler {

	/**
	 * PSO算法实例
	 */
	private PsoAlgorithm<PowerValue> psoAlgorithm;

	/**
	 * 构造函数
	 * 
	 * @param initParticleNum 初始粒子数量
	 * @param isImprovedParticle 是否使用改进粒子
	 */
	public PsoPowerScheduler(int initParticleNum, boolean isImprovedParticle) {

		DataFactory dataFactory = DataFactory.getInstance();
		PowerRange[] powerRangeArray = dataFactory.getPowerRangeArray();
		PowerVectorParticle[] ParticleSet = null;

		ParticleSet = new PowerVectorParticle[initParticleNum];

		if (isImprovedParticle) {
			for (int i = 0; i < initParticleNum; i++) {
				ParticleSet[i] = new PvImprovedParticle(powerRangeArray);
			}
		} else {
			for (int i = 0; i < initParticleNum; i++) {
				ParticleSet[i] = new PowerVectorParticle(powerRangeArray);
			}
		}

		psoAlgorithm = new PsoAlgorithm<PowerValue>(ParticleSet);
	}

	/**
	 * 执行功率调度算法
	 * 
	 * 使用PSO算法寻找最优的功率配置，并返回最优功率向量
	 * 
	 * @return 最优功率向量
	 */
	@Override
	public PowerVector schedule() {
		PowerValue[] bestLocation = psoAlgorithm
				.findBestLocation(PsoAlgorithmConstant.PSO_ITERATE_NUM);
		return new PowerVector(bestLocation);
	}

	/**
	 * 主函数，用于测试
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {

		final int initParticleNum = 10;
		PowerRange[] powerRangeArray = new PowerRange[2];
		powerRangeArray[0] = new PowerRange(1.0f, 10.0f);
		powerRangeArray[1] = new PowerRange(1.0f, 10.0f);

		Particle<RangeValue>[] ParticleSet = new EvaParticle[initParticleNum];

		for (int i = 0; i < initParticleNum; i++) {
			ParticleSet[i] = new EvaParticle(powerRangeArray);
		}

		PsoAlgorithm<RangeValue> psoAlgorithm = new PsoAlgorithm<RangeValue>(ParticleSet);
		RangeValue[] location = psoAlgorithm.findBestLocation(100);

		System.out.println("---bestValue:" + location[0].getValue() + "\t" + location[1].getValue()
				+ "\t" + psoAlgorithm.getGlobalBestValue());

	}
}