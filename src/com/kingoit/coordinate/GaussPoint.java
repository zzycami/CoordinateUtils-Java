package com.kingoit.coordinate;

/**
 * 高斯投影下的大地平面坐标点
 * @author Zhou.Zeyong
 *
 */
public class GaussPoint {
	private double x;
	
	private double y;
	
	private double h;
	
	public GaussPoint (double x, double y, double h) {
		this.x = x;
		this.y = y;
		this.h = h;
	}	

	public GaussPoint() {}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}
	
	 
}
