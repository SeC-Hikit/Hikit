package org.sc.common.rest;

public class TrailCoordinatesDto implements Coordinates {
    private int distanceFromTrailStart;
    private double latitude;
    private double longitude;
    private double altitude;

    public TrailCoordinatesDto() {
    }

    public TrailCoordinatesDto(double latitude, double longitude,
                               double altitude, int distanceFromTrailStart) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.distanceFromTrailStart = distanceFromTrailStart;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getAltitude() {
        return altitude;
    }

    public int getDistanceFromTrailStart() {
        return distanceFromTrailStart;
    }

    public void setDistanceFromTrailStart(int distanceFromTrailStart) {
        this.distanceFromTrailStart = distanceFromTrailStart;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}