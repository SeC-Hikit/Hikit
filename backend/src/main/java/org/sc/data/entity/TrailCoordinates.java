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
        this.distanceFromTrailStart = distanceFromTrailStart;
    }

    @Override
    public boolean equals(Object o) {
        TrailCoordinates that = (TrailCoordinates) o;
        return Double.compare(that.getDistanceFromTrailStart(), getDistanceFromTrailStart()) == 0 &&
        Double.compare(that.getLatitude(), getLatitude()) == 0 &&
        Double.compare(that.getLongitude(), getLongitude()) == 0;
    }

    @Override
    public boolean equals(Object o) {
        TrailCoordinates that = (TrailCoordinates) o;
        return Double.compare(that.getDistanceFromTrailStart(), getDistanceFromTrailStart()) == 0 &&
                Double.compare(that.getLatitude(), getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), getLongitude()) == 0;
    }
}
