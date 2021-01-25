package org.sc.data.entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class CoordinatesWithAltitude extends Coordinates {

    public static final String NO_CORRECT_PARAMS_ERROR_MESSAGE = "Error building coordinates: some values are found null, but that is not allowed";

    public final static String GEO_TYPE = "Point";
    public final static String COORDINATES = "coordinates";
    public final static String ALTITUDE = "altitude";

    private final double altitude;


    public CoordinatesWithAltitude(final double longitude,
                                   final double latitude,
                                   final double altitude) {
        super(Arrays.asList(longitude, latitude));
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
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
            assertCorrectValues();
            return new CoordinatesWithAltitude(longitude, latitude, altitude);
        }

        private void assertCorrectValues() {
            if(Stream.of(altitude, longitude, latitude).anyMatch(Objects::isNull)){
                throw new IllegalArgumentException(NO_CORRECT_PARAMS_ERROR_MESSAGE);
            }
        }
    }
}
