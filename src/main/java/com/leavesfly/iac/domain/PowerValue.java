package com.leavesfly.iac.domain;

import java.io.Serializable;

import com.leavesfly.iac.util.ArrayCloneUtil;
import com.leavesfly.iac.util.MathUtil;

public class PowerValue implements RangeValue, Serializable {

	private static final long serialVersionUID = 1L;

	private float value;
	private PowerRange range;

	public PowerValue(float value, PowerRange range) {
		this.value = value;
		this.range = range;
	}

	public PowerValue(PowerRange range) {
		this.range = range;
		this.value = MathUtil.nextFloat(range.getFrom(), range.getTo());

	}

	public PowerRange getRange() {
		return range;
	}

	public void setRange(PowerRange range) {
		this.range = range;
	}

	@Override
	public float getValue() {
		return value;
	}

	@Override
	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public float getFrom() {
		return range.getFrom();
	}

	@Override
	public void setFrom(float from) {
		range.setFrom(from);

	}

	@Override
	public float getTo() {
		return range.getTo();
	}

	@Override
	public void setTo(float to) {
		range.setTo(to);

	}

	@Override
	public boolean isInRange(float value) {
		return range.isInRange(value);
	}

	@Override
	public Object clone() {
		return new PowerValue(new PowerRange(range));
	}

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
