package com.leavesfly.iac.domain;

/**
 * 
 * @author LeavesFly
 *
 */
public interface RangeValue extends EnableClone {

	public void setValue(float value);

	public float getValue();

	public float getFrom();

	public void setFrom(float from);

	public float getTo();

	public void setTo(float to);

	public boolean isInRange(float value);
}
