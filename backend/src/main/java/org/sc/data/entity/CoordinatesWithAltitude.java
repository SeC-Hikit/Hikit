package org.sc.data.entity;

import org.sc.common.rest.Coordinates;

public class CoordinatesWithAltitude implements Coordinates {

    public static final int LONG_INDEX = 0;
    public static final int LAT_INDEX = 1;

    public final static String GEO_TYPE = "Point";
    public final static String COORDINATES = "coordinates";
    public final static String ALTITUDE = "altitude";

    private double longitude;
    private double latitude;
    private double altitude;

    public CoordinatesWithAltitude() { }

    public CoordinatesWithAltitude(final double latitude,
                                   final double longitude,
                                   final double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
