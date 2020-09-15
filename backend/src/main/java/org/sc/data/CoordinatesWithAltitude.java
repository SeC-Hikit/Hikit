package org.sc.data;

import java.util.Arrays;

public class CoordinatesWithAltitude extends Coordinates {

    public final static String GEO_TYPE = "Point";
    public final static String COORDINATES = "coordinates";

    private double altitude;

    public CoordinatesWithAltitude(final double longitude,
                                   final double latitude,
                                   final double altitude) {
        super(Arrays.asList(longitude, latitude));
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double distanceTo(CoordinatesWithAltitude coordinatesWithAltitude) {
        return 0;
    }


    public static final class CoordinatesWithAltitudeBuilder {
        private double altitude;
        private double longitude;
        private double latitude;

        private CoordinatesWithAltitudeBuilder() {
        }

        public static CoordinatesWithAltitudeBuilder aCoordinatesWithAltitude() {
            return new CoordinatesWithAltitudeBuilder();
        }

        public CoordinatesWithAltitudeBuilder withAltitude(double altitude) {
            this.altitude = altitude;
            return this;
        }

        public CoordinatesWithAltitudeBuilder withLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public CoordinatesWithAltitudeBuilder withLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public CoordinatesWithAltitude build() {
            return new CoordinatesWithAltitude(longitude, latitude, altitude);
        }
    }
}
