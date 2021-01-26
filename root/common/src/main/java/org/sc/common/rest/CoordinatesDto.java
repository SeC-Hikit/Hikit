package org.sc.common.rest;

public class CoordinatesDto implements Coordinates {
    private final double latitude;
    private final double longitude;
    private final double altitude;

    public CoordinatesDto(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }
}