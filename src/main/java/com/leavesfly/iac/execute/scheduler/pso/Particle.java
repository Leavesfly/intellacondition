package com.leavesfly.iac.execute.scheduler.pso;

import com.leavesfly.iac.config.PsoAlgorithmConstant;
import com.leavesfly.iac.domain.RangeValue;
import com.leavesfly.iac.util.ArrayCloneUtil;

/**
 * 粒子群算法的抽象粒子类
 * 
 * 该类是PSO算法中粒子的抽象基类，定义了粒子的基本属性和行为，
 * 包括位置、速度、目标值等，以及粒子的更新机制。
 * 
 * @param <T> 粒子位置和速度的类型，必须实现RangeValue接口
 */
public abstract class Particle<T extends RangeValue> {

	/**
	 * 初始惯性权重
	 */
	protected static final float INERTIA_WEIGHT_INIT = PsoAlgorithmConstant.PSO_INERTIA_WEIGHT_INIT;
	
	/**
	 * 结束惯性权重
	 */
	private static final float INERTIA_WEIGHT_END = PsoAlgorithmConstant.PSO_INERTIA_WEIGHT_END;
	
	/**
	 * 最大速度与位置比率
	 */
	private static final float MAX_SPEED_LOCATION_RATE = PsoAlgorithmConstant.PSO_MAX_SPEED_LOCATION_RATE;

	/**
	 * 个体学习因子
	 */
	protected static final float LEARN_RATE_1 = PsoAlgorithmConstant.PSO_LEARN_RATE_1;
	
	/**
	 * 社会学习因子
	 */
	protected static final float LEARN_RATE_2 = PsoAlgorithmConstant.PSO_LEARN_RATE_2;

	/**
	 * 常数0.5
	 */
	protected static final float HALF_OF_ONE = PsoAlgorithmConstant.PSO_HALF_OF_ONE;
	
	/**
	 * 迭代次数
	 */
	public final static int iterateNum = PsoAlgorithmConstant.PSO_ITERATE_NUM;
	
	/**
	 * 惯性权重变化频率
	 */
	public final static float inertiaWeightFrequency = (INERTIA_WEIGHT_INIT - INERTIA_WEIGHT_END)
			/ iterateNum;

	/**
	 * 粒子当前位置
	 */
	protected T[] location;
	
	/**
	 * 粒子当前速度
	 */
	protected T[] speed;
	
	/**
	 * 粒子当前目标值（适应度值）
	 */
	private float currentTargetValue;

	/**
	 * 粒子历史最优位置
	 */
	protected T[] bestLocation;
	
	/**
	 * 粒子历史最优目标值
	 */
	private float bestTargetValue;

	/**
	 * 当前迭代次数
	 */
	protected int currentIterateNum = 0;

	/**
	 * 受保护的构造函数，防止直接实例化
	 */
	protected Particle() {
	}

	/**
	 * 初始化粒子
	 * 
	 * @param location 初始位置
	 */
	protected void init(T[] location) {
		this.location = location;
		initSpeed();
		currentTargetValue = calTargetValue();

		bestLocation = ArrayCloneUtil.arrayDeepCopy(location);
		bestTargetValue = currentTargetValue;
	}

	/**
	 * 初始化粒子速度
	 * 
	 * 根据位置范围和最大速度位置比率计算最大速度，并随机初始化速度值
	 */
	private void initSpeed() {
		speed = ArrayCloneUtil.arrayDeepCopy(location);
		for (int i = 0; i < location.length; i++) {
			float maxSpeed = (location[i].getTo() - location[i].getFrom())
					* MAX_SPEED_LOCATION_RATE;
			speed[i].setValue((2 * (float) Math.random() - 1) * maxSpeed);
			speed[i].setFrom(-maxSpeed);
			speed[i].setTo(maxSpeed);
		}
	}

	/**
	 * 计算目标值的抽象方法
	 * 
	 * 由具体子类实现，根据粒子位置计算目标值（适应度值）
	 * 
	 * @param location 粒子位置
	 * @return 目标值
	 */
	protected abstract float calTargetValue(T[] location);

	/**
	 * 更新粒子状态
	 * 
	 * 根据全局最优位置更新粒子的速度和位置，
	 * 如果当前目标值优于历史最优目标值，则更新历史最优记录
	 * 
	 * @param globalBestLocation 全局最优位置
	 */
	public void updateParticle(T[] globalBestLocation) {

		genNewSpeedAndLocation(globalBestLocation);
		currentTargetValue = calTargetValue(location);

		if (currentTargetValue > bestTargetValue) {
			changeBestTargetAndLocation();
		}

		currentIterateNum++;
	}

	/**
	 * 计算当前目标值
	 * 
	 * @return 当前目标值
	 */
	private float calTargetValue() {
		return calTargetValue(location);
	}

	/**
	 * 生成新的速度和位置
	 * 
	 * @param globalBestLocation 全局最优位置
	 */
	private void genNewSpeedAndLocation(T[] globalBestLocation) {
		genNewSpeed(globalBestLocation);
		genNewLocation();
	}

	/**
	 * 生成新的速度
	 * 
	 * 根据PSO算法公式计算新速度：
	 * v = w * v + c1 * r1 * (pbest - x) + c2 * r2 * (gbest - x)
	 * 
	 * @param globalBestLocation 全局最优位置
	 */
	protected void genNewSpeed(T[] globalBestLocation) {
		for (int i = 0; i < globalBestLocation.length; i++) {
			float inertiaWeight = INERTIA_WEIGHT_INIT - inertiaWeightFrequency * currentIterateNum;

			float value = inertiaWeight * speed[i].getValue() + LEARN_RATE_1
					* (float) Math.random() * (bestLocation[i].getValue() - location[i].getValue())
					+ LEARN_RATE_2 * (float) Math.random()
					* (globalBestLocation[i].getValue() - location[i].getValue());

			if (speed[i].isInRange(value)) {
				speed[i].setValue(value);
			} else {
				speed[i].setValue(Math.random() >= HALF_OF_ONE ? speed[i].getFrom() : speed[i]
						.getTo());
			}
		}
	}

	/**
	 * 生成新的位置
	 * 
	 * 根据当前位置和速度计算新位置，并确保新位置在允许范围内
	 */
	protected void genNewLocation() {
		for (int i = 0; i < location.length; i++) {
			float value = location[i].getValue() + speed[i].getValue();
			if (value > location[i].getTo()) {
				location[i].setValue(location[i].getTo());
			} else if (value < location[i].getFrom()) {
				location[i].setValue(location[i].getFrom());
			} else {
				location[i].setValue(value);
			}
		}
	}

	/**
	 * 更新历史最优目标值和位置
	 */
	private void changeBestTargetAndLocation() {
		bestTargetValue = currentTargetValue;
		for (int i = 0; i < bestLocation.length; i++) {
			bestLocation[i].setValue(location[i].getValue());
		}
	}

	/**
	 * 获取历史最优位置
	 * 
	 * @return 历史最优位置
	 */
	public T[] getBestLocation() {
		return bestLocation;
	}

	/**
	 * 获取历史最优目标值
	 * 
	 * @return 历史最优目标值
	 */
	public float getBestTargetValue() {
		return bestTargetValue;
	}

}