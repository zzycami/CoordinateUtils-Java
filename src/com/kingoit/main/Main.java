package com.kingoit.main;

import com.kingoit.coordinate.CoordinateUtils;
import com.kingoit.coordinate.GaussPoint;
import com.kingoit.coordinate.GeodeticPoint;

public class Main {

	public static void main(String[] args) {
		CoordinateUtils coordinateUtils = new CoordinateUtils();
        GeodeticPoint geoPoint = new GeodeticPoint();
        geoPoint.setLatitude(coordinateUtils.translateDMSToAngle(29.41149626));
        geoPoint.setLongitude(coordinateUtils.translateDMSToAngle(121.3848571));
        geoPoint.setHeight(16.174);
        GaussPoint point = coordinateUtils.Xian80PointFromWGS84Point(geoPoint);
        System.out.println("X:" + point.getX() + ", Y:" +point.getY()+ ", Z:" + point.getH());
	}

}
