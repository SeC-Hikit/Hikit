package org.sc.data;

import java.util.Arrays;
import java.util.List;

public class Coordinates {

    public static final int LONG_INDEX = 0;
    public static final int LAT_INDEX = 1;
    private final List<Double> values;

    public Coordinates(final List<Double> values) {
        if(values.size() > 2){
            throw new IllegalArgumentException("No more than a two parameters list is expected");
        }
        this.values = values;
    }

    public double getLongitude() {
        return values.get(LONG_INDEX);
    }

    public double getLatitude() {
        return values.get(LAT_INDEX);
    }

    public List<Double> getValues() {
        return values;
    }

    public static final class CoordinateBuilder {
        private double longitude;
        private double latitude;

        private CoordinateBuilder() {
        }

        public static CoordinateBuilder aCoordinate() {
            return new CoordinateBuilder();
        }

        public CoordinateBuilder withLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public CoordinateBuilder withLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Coordinates build() {
            return new Coordinates(Arrays.asList(longitude, latitude));
        }
    }
}
