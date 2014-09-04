package com.kingoit.coordinate;

/**
 * 参心大地坐标系中的点，就是经纬度坐标点
 * @author Zhou.Zeyong
 *
 */
public class GeodeticPoint {
	/**
	 * 纬度
	 */
	private double latitude;
	
	/**
	 * 经度
	 */
	private double longitude;
	
	/**
	 * 海拔
	 */
	private double height;
	
	public GeodeticPoint (double latitude, double longitude, double height) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}

	public GeodeticPoint() {}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
