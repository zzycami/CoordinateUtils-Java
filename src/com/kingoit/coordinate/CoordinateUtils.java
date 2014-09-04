package com.kingoit.coordinate;

public class CoordinateUtils {
	/**
	 * 参心大地坐标转换为参心空间直角坐标
	 * 坐标转换的公式为：
	 * X = (N+H)*cosB*cosL
	 * Y = (N+H)*cosB*sinL
	 * Z = [N*(1-e^2) + H]*sinB
	 * X, Y, Z分别代表空间直角坐标系中的点的三个参数
	 * B, L, H分别表示纬度， 经度， 和海拔
	 * N表示椭球面卯酉圈的曲率半径，e^2为椭球的第一偏心率，a、b 椭球的长短半径，f 椭球扁率，W为第一辅助系数
	 * e^2 = (a*a - b*b)/(a*a) = 1 - pow(b/a, 2)
	 * W = sqrt(1 - e^2*sin(B)^2)
	 * N = a/W
	 * @param point 大地坐标点
	 * @param ellipsoid 坐标转换所在的椭球
	 * @return 相对应的空间直角坐标系中的点
	 */
	public RectangularPlanePoint rectangularPlaneCoordinateFromGeodeticCoordinates(GeodeticPoint point, Ellipsoid ellipsoid) {
		RectangularPlanePoint recttangularPlanePoint = new RectangularPlanePoint();
	    
	    double latitude = this.translateAngle(point.getLatitude());
	    double longitude = this.translateAngle(point.getLongitude());
	    double height = point.getHeight();
	    
	    double W = Math.sqrt(1 - ellipsoid.getE2() * Math.pow(Math.sin(latitude), 2));
	    double N = ellipsoid.getA()/W;
	    
	    recttangularPlanePoint.setX((N + height)*Math.cos(latitude)*Math.cos(longitude));
	    
	    recttangularPlanePoint.setY((N + height)*Math.cos(latitude)*Math.sin(longitude));
	    
	    recttangularPlanePoint.setZ((N*(1 - ellipsoid.getE2()) + height)*Math.sin(latitude));
	    
	    return recttangularPlanePoint;
	}
	
	/**
	 * 用布尔莎七参数公式计算两个不同的空间直角坐标系之间的转化
	 * @param point 需要转化的第一个坐标的坐标点
	 * @param parameter 七参
	 * @return 对应坐标系中的点
	 */
	public RectangularPlanePoint rectangularPlaneTanslate(RectangularPlanePoint point, MapParameter parameter) {
		RectangularPlanePoint toPoint = new RectangularPlanePoint();
	    
	    double deltaX = parameter.getDeltaX();
	    double deltaY = parameter.getDeltaY();
	    double deltaZ = parameter.getDeltaZ();
	    //标准参数中给出的单位是秒，这里要转成弧度
	    double rotateX = this.translateAngle(parameter.getRotateX()/3600.0);
	    double rotateY = this.translateAngle(parameter.getRotateY()/3600.0);
	    double rotateZ = this.translateAngle(parameter.getRotateZ()/3600.0);
	    // 标准参数中给出的值的单位是ppm，百万分之一米
	    double k = 1.0 / parameter.getK() / 10000000.0;
	    
	    toPoint.setX(deltaX + (1 + k)*point.getX() + rotateZ*point.getY() - rotateY*point.getZ());
	    toPoint.setY(deltaY + (1 + k)*point.getY() - rotateZ*point.getX() + rotateX*point.getZ());
	    toPoint.setZ(deltaZ + (1 + k)*point.getZ() + rotateY*point.getX() - rotateX*point.getY());
	    
	    return toPoint;
	}
	
	/**
	 * 空间直角坐标系转大地坐标系
	 * L = arctan(Y/X)
	 * B的计算通常采用牛顿迭代法来近似计算，理论上可以达到任意想要的精度
	 * B(0) = arctan(Z/sqrt(X^2 + Y^2))
	 * B(i+1) = arctan((Z + N(i)*e^2*sin(B(i)))/sqrt(X^2 + Y^2))
	 * H = Z/sin(B) - N*(1 - e^2)
	 * @param point 空间直角坐标系中的点
	 * @param ellipsoid 计算所基于的椭球定义
	 * @return 相对应的大地坐标系中的点
	 */
	public GeodeticPoint geodeticCoordinatesFromRectangularPlaneCoordinate(RectangularPlanePoint point, Ellipsoid ellipsoid) {
		GeodeticPoint geodeticPoint = new GeodeticPoint();
	    
	    geodeticPoint.setLongitude(Math.atan((point.getY()/point.getX())));
	    if(geodeticPoint.getLongitude() < 0) {
	        geodeticPoint.setLongitude(geodeticPoint.getLongitude() + Math.PI);
	    }
	    
	    double b0 = Math.atan(point.getZ()/Math.sqrt(point.getX()*point.getX() + point.getY()*point.getY()));// 纬度迭代的起始值
	    double delta = Math.PI / (180000 * 3600);// 迭代的精度控制，这个值越小，精度越高
	    double N = ellipsoid.getA()/Math.sqrt(1 - ellipsoid.getE2() * Math.pow(Math.sin(b0), 2));
	    double b1 = 0;
	    
	    while (b0 - b1 >= delta) {
	        b1 = b0;
	        b0 = Math.atan((point.getZ() + N*ellipsoid.getE2()*Math.sin(b1))/Math.sqrt(point.getX()*point.getX() + point.getY()*point.getY()));
	        N = ellipsoid.getA()/Math.sqrt(1 - ellipsoid.getE2() * Math.pow(Math.sin(b0), 2));
	    }
	    geodeticPoint.setLatitude(b0);
	    if (geodeticPoint.getLatitude() < 0) {
	        geodeticPoint.setLatitude(geodeticPoint.getLatitude() + Math.PI);
	    }
	    
	    geodeticPoint.setHeight(point.getZ()/Math.sin(geodeticPoint.getLatitude()) - N*(1 - ellipsoid.getE2()));
	    // 将他们的弧度转成角度
	    geodeticPoint.setLongitude(this.tanslateDegree(geodeticPoint.getLongitude()));
	    geodeticPoint.setLatitude(this.tanslateDegree(geodeticPoint.getLatitude()));
	    return geodeticPoint;
	}
	
	/**
	 * 弧度转角度
	 * @param degree 弧度
	 * @return 角度
	 */
	public double tanslateDegree(double degree) {
		return degree*180.0/Math.PI;
	}

	/**
	 * 角度转弧度
	 * @param angle 角度
	 * @return 弧度
	 */
	public double translateAngle(double angle) {
		return angle/180.0*Math.PI;
	}
	
	/**
	 * 将度分秒转化成度数
	 * @param dms
	 * @return 度数
	 */
	public double translateDMSToAngle(double dms) {
		int Deg,Min;
	    double Sec;
	    Deg=(int)dms;
	    Min=(int)((dms-Deg)*100);
	    Sec=((dms-Deg)*100-Min)*100;
	    return (Deg+Min/60.0+Sec/3600.0);
	}
	
	/**
	 * 高斯投影正算，计算大地坐标系（就是经纬度坐标系）中的点投影到2D平面坐标系中的点
	 * @param geoPoint
	 * @param ellipsoid
	 * @param zoneWidth
	 * @return
	 */
	public GaussPoint gaussProjection(GeodeticPoint geoPoint, Ellipsoid ellipsoid, int zoneWidth) {
		GaussPoint point = new GaussPoint();
	    
	    // 计算带号
	    int projectionNumber = 0;
	    // 计算代号中央经线
	    double longtitude0 = 0;
	    if (zoneWidth == 3) {
	        projectionNumber = (int)(geoPoint.getLongitude()/zoneWidth);
	        longtitude0 = projectionNumber*zoneWidth;
	    }else if(zoneWidth == 6) {
	        projectionNumber = (int)(geoPoint.getLongitude()/zoneWidth) + 1;
	        longtitude0 = projectionNumber*zoneWidth - zoneWidth/2.0;
	    }
	    
	    
	    // 将角度转化为弧度
	    longtitude0 = this.translateAngle(longtitude0);
	    double longtitude = this.translateAngle(geoPoint.getLongitude());
	    double lantitude = this.translateAngle(geoPoint.getLatitude());
	    
	    // 计算椭球面卯酉圈的曲率半径
	    double N = ellipsoid.getA()/Math.sqrt(1 - ellipsoid.getE2() * Math.pow(Math.sin(lantitude), 2));
	    
	    // 计算公式中的参数T
	    double T = Math.pow(Math.tan(lantitude), 2);
	    
	    // 计算公式中用到的参数C
	    double C = ellipsoid.getEe() * Math.pow(Math.cos(lantitude), 2);
	    
	    // 计算公式中用到的参数A
	    double A = (longtitude - longtitude0) * Math.cos(lantitude);
	    
	    // 算克拉索夫斯基椭球中子午弧长M，数学中常用X表示
	    double M = ellipsoid.getA() * ((1 - ellipsoid.getE2()/4 - 3*Math.pow(ellipsoid.getE2(), 2)/64 - 5*Math.pow(ellipsoid.getE2(), 3)/256)*lantitude - (3*ellipsoid.getE2()/8 + 3*Math.pow(ellipsoid.getE2(), 2)/32 + 45*Math.pow(ellipsoid.getE2(), 3)/1024)*Math.sin(2*lantitude) + (15*Math.pow(ellipsoid.getE2(), 2)/256 + 45*Math.pow(ellipsoid.getE2(), 3)/1024)*Math.sin(4*lantitude) - (35*Math.pow(ellipsoid.getE2(), 3)/3072)*Math.sin(6*lantitude));
	    
	    //因为是以赤道为Y轴的，与我们南北为Y轴是相反的，所以xy与高斯投影的标准xy正好相反;
	    point.setX(N*(A + (1 - T + C)*Math.pow(A, 3)/6 + (5 - 18*T + Math.pow(T, 2) + 14*C - 58*ellipsoid.getEe())*Math.pow(A, 5)/120));
	    
	    point.setY(M + N*Math.tan(lantitude)*(A*A/2 + (5 - T + 9*C + 4*C*C)*Math.pow(A, 4)/24 + (61 - 58*T + T*T + 270*C - 330*ellipsoid.getEe())*Math.pow(A, 6)/720));
	    
	    // 在我国 坐标都是正的， 坐标的最大值（在赤道上）约为330km。为了避免出现负的横坐标，可在横坐标上加上500 000m。
	    point.setX(point.getX() + projectionNumber*1000000L + 500000L);
	    point.setH(geoPoint.getHeight());
	    
	    return point;
	}
	
	/**
	 * 计算高斯投影反算中非常重要的参数Bf（即底点纬度）
	 * @param x 大地平面坐标点的X值
	 * @param ellipsoid 计算所基于的椭球
	 * @return 底点纬度
	 */
	private double calculateBf(double x, Ellipsoid ellipsoid) {
	    double e2 = ellipsoid.getE2();
	    double e4 = Math.pow(ellipsoid.getE2(), 2);
	    double e6 = Math.pow(ellipsoid.getE2(), 3);
	    double e8 = Math.pow(ellipsoid.getE2(), 4);
	    double e10 = Math.pow(ellipsoid.getE2(), 5);
	    double e12 = Math.pow(ellipsoid.getE2(), 6);
	    double e14 = Math.pow(ellipsoid.getE2(), 7);
	    double e16 = Math.pow(ellipsoid.getE2(), 8);
	    
	    double c0 = 1 + e2 / 4 + 7 * e4 / 64 + 15 * e6 / 256 + 579 * e8 / 16384 + 1515 * e10 / 65536 + 16837 * e12 / 1048576 + 48997 * e14 / 4194304 + 9467419 * e16 / 1073741824;
	    
	    c0 = ellipsoid.getA()/c0;
	    
	    double b0 = x/c0;
	    
	    double d1 = 3 * e2 / 8 + 45 * e4 / 128 + 175 * e6 / 512 + 11025 * e8 / 32768 + 43659 * e10 / 131072 + 693693 * e12 / 2097152 + 10863435 * e14 / 33554432;
	    
	    double d2 = -21 * e4 / 64 - 277 * e6 / 384 - 19413 * e8 / 16384 - 56331 * e10 / 32768 - 2436477 * e12 / 1048576 - 196473 * e14 / 65536;
	    
	    double d3 = 151 * e6 / 384 + 5707 * e8 / 4096 + 53189 * e10 / 163840 + 4599609 * e12 / 655360 + 15842375 * e14 / 1048576;
	    
	    double d4 = -1097 * e8 / 2048 - 1687 * e10 / 640 - 3650333 * e12 / 327680 - 114459079 * e14 / 27525120;
	    
	    double d5 = 8011 * e10 / 1024 + 874457 * e12 / 98304 + 216344925 * e14 / 3670016;
	    
	    double d6 = -682193 * e12 / 245760 - 46492223 * e14 / 1146880;
	    
	    double d7 = 36941521 * e14 / 3440640;
	    
	    double bf;
	    bf=b0+Math.sin(2*b0)*(d1+Math.sin(b0)*Math.sin(b0)*(d2+Math.sin(b0)*Math.sin(b0)*(d3+Math.sin(b0)*Math.sin(b0)*(d4+Math.sin(b0)*Math.sin(b0)*(d5+Math.sin(b0)*Math.sin(b0)*(d6+d7*Math.sin(b0)*Math.sin(b0)))))));
	    return bf;
	}
	
	/**
	 * 高斯投影反算, 计算带带号的坐标
	 * @param gaussPoint 所需要计算转化的高斯平面投影点
	 * @param ellipsiod 计算所基于的椭球定义
	 * @param zoneWidth 带宽，一般是3度分带或者6度分带
	 * @return 经过高斯反算所得到的经纬度坐标
	 */
	public GeodeticPoint gaussProjectionReverse(GaussPoint gaussPoint, Ellipsoid ellipsiod, int zoneWidth) {
	    // 计算带号
	    int projectionNumber = (int)(gaussPoint.getX()/1000000L);
	    gaussPoint.setX(gaussPoint.getX() - projectionNumber*1000000L);
	    return this.gaussProjectionReverse(gaussPoint, ellipsiod, zoneWidth, projectionNumber);
	}
	
	/**
	 * 高斯投影反算, 不带带号， 直接给出所处的带号
	 * @param gaussPoint 所需要计算转化的高斯平面投影点
	 * @param ellipsiod 计算所基于的椭球定义
	 * @param zoneWidth 带宽，一般是3度分带或者6度分带
	 * @param projectionNumber 带号， 因为坐标中没有给出带号，所以这里需要自己指定带号
	 * @return 经过高斯反算所得到的经纬度坐标
	 */
	public GeodeticPoint gaussProjectionReverse(GaussPoint gaussPoint, Ellipsoid ellipsiod, int zoneWidth, int projectionNumber) {
	    GeodeticPoint point = new GeodeticPoint();
	    // 计算代号中央经线
	    double longtitude0 = 0;
	    if(zoneWidth == 3) {
	        longtitude0 = projectionNumber*zoneWidth;
	    }else if(zoneWidth == 6) {
	        longtitude0 = (projectionNumber - 1)*zoneWidth + 0.5*zoneWidth;
	    }
	    
	    longtitude0 = this.translateAngle(longtitude0);
	    
	    // 去除代号和偏移值
	    double x = gaussPoint.getY();
	    double y = gaussPoint.getX() - 500000L;
	    
	    // 计算反算公式中的各个符号的值
	    double Bf = this.calculateBf(x, ellipsiod);
	    double Tf = Math.tan(Bf);
	    double Nf2 = ellipsiod.getEe()*Math.cos(Bf)*Math.cos(Bf);
	    double Wf = Math.sqrt(1 - ellipsiod.getE2()*Math.sin(Bf)*Math.sin(Bf));
	    double Mf = ellipsiod.getA()*(1 - ellipsiod.getE2())/Math.pow(Wf, 3);
	    double Nf = ellipsiod.getA()/Wf;
	    
	    point.setLongitude(longtitude0 +  y/(Nf* Math.cos(Bf)) - (1 + 2*Tf*Tf + Nf2)*Math.pow(y, 3)/(6*Math.pow(Nf, 3)*Math.cos(Bf)) + (5 + 28*Tf*Tf + 24*Math.pow(Tf, 4))*Math.pow(5, 5)/(120*Math.pow(Nf, 5)*Math.cos(Bf)));
	    
	    point.setLatitude(Bf - Tf*y*y/(2*Mf*Nf) + Tf*(5 + 3*Tf + Nf2 - 9*Nf2*Tf*Tf)*Math.pow(y, 4)/(24*Mf*Math.pow(Nf, 3)) - Tf*(61 + 90*Tf*Tf + 45*Math.pow(Tf, 4))*Math.pow(y, 6)/(720*Mf*Math.pow(Nf, 5)));
	    
	    point.setLongitude(this.tanslateDegree(point.getLongitude()));
	    point.setLatitude(this.tanslateDegree(point.getLatitude()));
	    point.setHeight(gaussPoint.getH());
	    
	    return point;
	}
	
	
	public GaussPoint Xian80PointFromWGS84Point(GeodeticPoint point) {
	    
	    // 84坐标点先转空间直角坐标点
	    RectangularPlanePoint rectPoint84 = this.rectangularPlaneCoordinateFromGeodeticCoordinates(point, Ellipsoid.WGS84());
	    
	    // 84空间坐标点转80空间坐标点
	    MapParameter parameter = new MapParameter();
	    //杭州参数
//	    parameter.deltaX = 202.634955018375;
//	    parameter.deltaY = 80.880255411162;
//	    parameter.deltaZ = 66.252987028366;
//	    parameter.rotateX = 1.227450;
//	    parameter.rotateY = 2.290472;
//	    parameter.rotateZ = -2.866066;
//	    parameter.k = -1.69905481167421;
	    
	    // 宁波参数 84转80参数
	    parameter.setDeltaX(219.24366659464);
	    parameter.setDeltaY(85.294116277735);
	    parameter.setDeltaZ(72.290279203019);
	    parameter.setRotateX(1.150130);
	    parameter.setRotateY(2.901362);
	    parameter.setRotateZ(-3.161284);
	    parameter.setK(-1.427491005181);
	    
	    RectangularPlanePoint rectoPoint80 = this.rectangularPlaneTanslate(rectPoint84, parameter);
	    
	    // 80空间坐标点转80经纬度坐标
	    GeodeticPoint geoPoint80 = this.geodeticCoordinatesFromRectangularPlaneCoordinate(rectoPoint80, Ellipsoid.Xian80());
	    
	    // 80经纬度坐标点转80平面坐标
	    GaussPoint toPoint = this.gaussProjection(geoPoint80, Ellipsoid.Xian80(), 3);
	    
	    return toPoint;
	}
}
