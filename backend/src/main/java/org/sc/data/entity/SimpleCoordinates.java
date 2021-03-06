package org.sc.data.entity;

import java.util.Arrays;
import java.util.List;

public class SimpleCoordinates {
    private final Double latitude;
    private final Double longitude;

    public SimpleCoordinates(final Double longitude,
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
