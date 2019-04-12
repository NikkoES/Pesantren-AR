package io.github.nikkoes.pesantrenar.utils;

import java.math.BigDecimal;

import static java.lang.Math.toRadians;

public class Algorithm {

    private final static int EARTH_RADIUS = 6371;

    public static double calculateHarversine(double myLat, double myLong, double pesantrenLat, double pesantrenLong) {
        double dLat = toRadians(pesantrenLat - myLat);
        double dLon = toRadians(pesantrenLong - myLong);
        myLat = toRadians(myLat);
        pesantrenLat = toRadians(pesantrenLat);

        float a = (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(myLat) * Math.cos(pesantrenLat));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        return convertTwo(EARTH_RADIUS * c);
    }

    private static double convertTwo(double n) {
        return BigDecimal.valueOf(n).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double calculateBearing(double lat1_D, double lat2_D, double lng1_D, double lng2_D) {
        double lat1_R = Math.toRadians(lat1_D);
        double lat2_R = Math.toRadians(lat2_D);
        double lng1_R = Math.toRadians(lng1_D);
        double lng2_R = Math.toRadians(lng2_D);

        double y = Math.sin(lng2_R - lng1_R) * Math.cos(lat2_R);
        double x = Math.cos(lat1_R) * Math.sin(lat2_R) - Math.sin(lat1_R) * Math.cos(lat2_R) * Math.cos(lng2_R - lng1_R);

        return Math.toDegrees(Math.atan2(y, x));
    }

}
