package com.leavesfly.iac.domain;

public class GeoPoint {
	private int x;
	private int y;

	public GeoPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getDistance(GeoPoint geoPoint) {
		return (float) (Math.sqrt(Math.pow(geoPoint.getX() - x, 2) + Math.pow(geoPoint.getY() - y, 2)));
	}
}
