package com.leavesfly.iac.execute.domain;

public class UserTempRange {
	private float from;
	private float to;

	public UserTempRange(float from, float to) {
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

	public boolean isInRange(float temp) {
		if (temp > to || temp < from) {
			return false;
		}
		return true;
	}

}
