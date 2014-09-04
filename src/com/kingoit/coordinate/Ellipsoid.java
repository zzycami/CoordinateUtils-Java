package com.kingoit.coordinate;


/**
 * 椭球定义
 * @author Zhou.Zeyong
 *
 */
public class Ellipsoid {
	/**
	 * 椭球的长半轴
	 */
	private double a;
	
	/**
	 * 椭球的短半轴
	 */
	private double b;
	
	/**
	 * 扁率
	 */
	private double f;
	
	/**
	 * 第一偏心率
	 */
	private double e2;
	
	/**
	 * 第二偏心率
	 */
	private double ee;
	
	public Ellipsoid(double a, double b) {
		this.a = a;
		this.b = b;
		this.f = (a - b)/a;
	}
	
	public Ellipsoid(double a, double b, double f) {
		this.a = a;
		this.b = b;
		this.f = f;
	}
	
	public static Ellipsoid WGS84() {
		Ellipsoid wgs84 = new Ellipsoid(6378137, 6356752.314, 1/298.257223563);
		wgs84.e2 = 0.006694379989;
		return wgs84;
	}
	
	public static Ellipsoid Xian80() {
		Ellipsoid xian80 = new Ellipsoid(6378140, 6356755.2882, 1/298.257);
		xian80.e2 = 0.00669438499959;
		return xian80;
	}
	
	public static Ellipsoid BeiJin54() {
		Ellipsoid beijin54 = new Ellipsoid(6378245, 6356863.019, 1/298.3);
		beijin54.e2 = 0.006693421623;
		return beijin54;
	}
	
	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getE2() {
		if (this.e2 == 0) {
			this.e2 = (this.a*this.a - this.b*this.b)/(this.a*this.a);
		}
		return e2;
	}

	public void setE2(double e2) {
		this.e2 = e2;
	}

	public double getEe() {
		if (ee == 0) {
			this.ee = this.e2/(1 - this.e2);
		}
		return ee;
	}

	public void setEe(double ee) {
		this.ee = ee;
	}
}
