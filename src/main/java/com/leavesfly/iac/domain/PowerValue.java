package com.leavesfly.iac.domain;

import java.io.Serializable;

import com.leavesfly.iac.util.ArrayCloneUtil;
import com.leavesfly.iac.util.MathUtil;

/**
 * 功率值类
 * 
 * 该类表示一个具体的功率值及其取值范围，实现了RangeValue接口和Serializable接口，
 * 支持功率值的设置、获取和范围检查，并实现了克隆功能。
 */
public class PowerValue implements RangeValue, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 功率值
	 */
	private float value;
	
	/**
	 * 功率取值范围
	 */
	private PowerRange range;

	/**
	 * 构造函数
	 * 
	 * @param value 功率值
	 * @param range 功率取值范围
	 */
	public PowerValue(float value, PowerRange range) {
		this.value = value;
		this.range = range;
	}

	/**
	 * 构造函数，随机生成范围内的功率值
	 * 
	 * @param range 功率取值范围
	 */
	public PowerValue(PowerRange range) {
		this.range = range;
		this.value = MathUtil.nextFloat(range.getFrom(), range.getTo());

	}

	/**
	 * 获取功率取值范围
	 * 
	 * @return 功率取值范围
	 */
	public PowerRange getRange() {
		return range;
	}

	/**
	 * 设置功率取值范围
	 * 
	 * @param range 功率取值范围
	 */
	public void setRange(PowerRange range) {
		this.range = range;
	}

	/**
	 * 获取功率值
	 * 
	 * @return 功率值
	 */
	@Override
	public float getValue() {
		return value;
	}

	/**
	 * 设置功率值
	 * 
	 * @param value 功率值
	 */
	@Override
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * 获取范围起始值
	 * 
	 * @return 范围起始值
	 */
	@Override
	public float getFrom() {
		return range.getFrom();
	}

	/**
	 * 设置范围起始值
	 * 
	 * @param from 范围起始值
	 */
	@Override
	public void setFrom(float from) {
		range.setFrom(from);

	}

	/**
	 * 获取范围结束值
	 * 
	 * @return 范围结束值
	 */
	@Override
	public float getTo() {
		return range.getTo();
	}

	/**
	 * 设置范围结束值
	 * 
	 * @param to 范围结束值
	 */
	@Override
	public void setTo(float to) {
		range.setTo(to);

	}

	/**
	 * 判断指定值是否在范围内
	 * 
	 * @param value 待判断的值
	 * @return 如果在范围内返回true，否则返回false
	 */
	@Override
	public boolean isInRange(float value) {
		return range.isInRange(value);
	}

	/**
	 * 克隆方法
	 * 
	 * @return 克隆后的功率值对象
	 */
	@Override
	public Object clone() {
		return new PowerValue(new PowerRange(range));
	}

	/**
	 * 主函数，用于测试
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		// System.out.println(new PowerValue(new PowerRange(1000,
		// 5000)).getValue());

		PowerValue[] p = new PowerValue[2];
		p[0] = new PowerValue(new PowerRange(1000, 5000));

		PowerValue[] p2 = ArrayCloneUtil.arrayDeepCopy(p);
		System.out.println(p[0].getValue() + "\t" + p[0].getFrom() + "," + p[0].getTo());
		System.out.println(p.toString());

		System.out.println("--------------------------");
		System.out.println(p2[0].getValue() + "\t" + p2[0].getFrom() + "," + p2[0].getTo());
		System.out.println(p2.toString());

		System.out.println("--------------change------------");
		p2[0].setValue(11111);
		p2[0].setFrom(3232323);
		p2[0].setTo(1232);

		System.out.println(p[0].getValue() + "\t" + p[0].getFrom() + "," + p[0].getTo());

		System.out.println("--------------------------");
		System.out.println(p2[0].getValue() + "\t" + p2[0].getFrom() + "," + p2[0].getTo());

	}

}