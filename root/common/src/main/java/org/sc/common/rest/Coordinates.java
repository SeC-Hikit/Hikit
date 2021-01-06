package org.sc.common.rest;

import java.util.List;

abstract class Coordinates {

    public static final int PARAMS_LIMIT = 2;

    public static final int LONG_INDEX = 0;
    public static final int LAT_INDEX = 1;
    private final List<Double> values;

    public Coordinates(final List<Double> values) {
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

}
