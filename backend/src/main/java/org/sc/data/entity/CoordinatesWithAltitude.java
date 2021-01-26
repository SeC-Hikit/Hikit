package org.sc.data.entity;

import org.sc.common.rest.Coordinates;

public class CoordinatesWithAltitude implements Coordinates {

    public static final int LONG_INDEX = 0;
    public static final int LAT_INDEX = 1;

    public final static String GEO_TYPE = "Point";
    public final static String COORDINATES = "coordinates";
    public final static String ALTITUDE = "altitude";

    private final double longitude;
    private final double latitude;
    private final double altitude;

    public CoordinatesWithAltitude(final double longitude,
                                   final double latitude,
                                   final double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
