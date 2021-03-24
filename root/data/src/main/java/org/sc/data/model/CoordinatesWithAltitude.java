package org.sc.data.model;

public class CoordinatesWithAltitude implements Coordinates {

    public static final int LONG_INDEX = 0;
    public static final int LAT_INDEX = 1;

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
