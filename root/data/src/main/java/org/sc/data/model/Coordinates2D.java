package org.sc.data.model;

import java.util.Arrays;
import java.util.List;

public class Coordinates2D {
    private final Double latitude;
    private final Double longitude;

    public Coordinates2D(final Double longitude,
                         final Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List<Double> getAsList(){
        return Arrays.asList(longitude, latitude);
    }
}
