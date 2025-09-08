package com.leavesfly.iac.domain;

/**
 * 地理位置点类
 * 
 * 该类表示一个二维空间中的地理位置点，包含x和y坐标，
 * 并提供计算与其他点之间距离的方法。
 */
public class GeoPoint {
	/**
	 * X坐标
	 */
	private int x;
	
	/**
	 * Y坐标
	 */
	private int y;

	/**
	 * 构造函数
	 * 
	 * @param x X坐标
	 * @param y Y坐标
	 */
	public GeoPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * 获取X坐标
	 * 
	 * @return X坐标
	 */
	public int getX() {
		return x;
	}

	/**
	 * 设置X坐标
	 * 
	 * @param x X坐标
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 获取Y坐标
	 * 
	 * @return Y坐标
	 */
	public int getY() {
		return y;
	}

	/**
	 * 设置Y坐标
	 * 
	 * @param y Y坐标
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 计算与另一个地理位置点之间的欧几里得距离
	 * 
	 * @param geoPoint 另一个地理位置点
	 * @return 两点之间的距离
	 */
	public float getDistance(GeoPoint geoPoint) {
		return (float) (Math.sqrt(Math.pow(geoPoint.getX() - x, 2) + Math.pow(geoPoint.getY() - y, 2)));
	}
}