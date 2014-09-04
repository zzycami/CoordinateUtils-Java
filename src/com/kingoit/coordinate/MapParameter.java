package com.kingoit.coordinate;

/**
 * 
 * @author Zhou.Zeyong
 * The 9 transform parameter in 3D map
 */
public class MapParameter {
	private double deltaX;
	
	private double deltaY;
	
	private double deltaZ;
	
	private double rotateX;
	
	private double rotateY;
	
	private double rotateZ;
	
	private double k;
	
	private double offsetX;
	
	private double offsetY;
	
	public boolean isNull() {
		if(this.deltaX == 0 &&
				this.deltaY == 0 &&
				this.deltaZ == 0 &&
				this.rotateX == 0 &&
				this.rotateY == 0 &&
				this.rotateZ == 0 &&
				this.k == 0 &&
				this.offsetX == 0 &&
				this.offsetY == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public double getDeltaX() {
		return deltaX;
	}
	public void setDeltaX(double deltaX) {
		this.deltaX = deltaX;
	}
	public double getDeltaY() {
		return deltaY;
	}
	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}
	public double getDeltaZ() {
		return deltaZ;
	}
	public void setDeltaZ(double deltaZ) {
		this.deltaZ = deltaZ;
	}
	public double getRotateX() {
		return rotateX;
	}
	public void setRotateX(double rotateX) {
		this.rotateX = rotateX;
	}
	public double getRotateY() {
		return rotateY;
	}
	public void setRotateY(double rotateY) {
		this.rotateY = rotateY;
	}
	public double getRotateZ() {
		return rotateZ;
	}
	public void setRotateZ(double rotateZ) {
		this.rotateZ = rotateZ;
	}
	public double getK() {
		return k;
	}
	public void setK(double k) {
		this.k = k;
	}
	public double getOffsetX() {
		return offsetX;
	}
	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}
	public double getOffsetY() {
		return offsetY;
	}
	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}
	
	@Override
	public String toString() {
		return "deltaX:" + String.valueOf(this.deltaX) + 
				"deltaY:" + String.valueOf(this.deltaY) +
				"deltaZ:" + String.valueOf(this.deltaZ) +
				"rotateX:" + String.valueOf(this.rotateX) +
				"rotateY:" + String.valueOf(this.rotateY) +
				"rotateZ:" + String.valueOf(this.rotateZ) +
				"k:" + String.valueOf(this.k) +
				"offsetX:" + String.valueOf(this.offsetX) +
				"offsetY:" + String.valueOf(this.offsetY);
	}
	
	
}
