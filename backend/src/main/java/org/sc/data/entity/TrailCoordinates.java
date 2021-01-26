package org.sc.data.entity;

public class TrailCoordinates extends CoordinatesWithAltitude {

    public final static String DISTANCE_FROM_START = "distFromStart";

    private int distanceFromTrailStart;
    private double latitude;
    private double longitude;
    private double altitude;

    public TrailCoordinates() {
        super(0.0, 0.0, 0.0);
    }

    public TrailCoordinates(final double latitude,
                            final double longitude,
                            final double altitude,
                            final int distanceFromTrailStart) {
        super(longitude, latitude, altitude);
        this.longitude = longitude;
        this.latitude = latitude;
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

    public int getDistanceFromTrailStart() {
        return distanceFromTrailStart;
    }

    @Override
    public boolean equals(Object o) {
        TrailCoordinates that = (TrailCoordinates) o;
        return Double.compare(that.getDistanceFromTrailStart(), getDistanceFromTrailStart()) == 0 &&
                Double.compare(that.getLatitude(), getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), getLongitude()) == 0;
    }
}
