package com.leavesfly.iac.domain;

import java.io.Serializable;

public class PowerRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private float from;
	private float to;

	public PowerRange(PowerRange powerRange) {
		from = powerRange.from;
		to = powerRange.to;
	}

	public PowerRange(float from, float to) {
		this.from = from;
		this.to = to;
	}

	public float getFrom() {
		return from;
	}

	public void setFrom(float from) {
		this.from = from;
	}

	public float getTo() {
		return to;
	}

	public void setTo(float to) {
		this.to = to;
	}

	public boolean isInRange(float value) {
		if (value > to || value < from) {
			return false;
		}
		return true;
	}

	public PowerValue genInitValue() {
		PowerValue powerValue = new PowerValue(new PowerRange(this));
		return powerValue;
	}

}
