package org.sc.data.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Coordinates2D {
    private final Double latitude;
    private final Double longitude;

    public Coordinates2D(final Double longitude,
                         final Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public List<Double> getAsList(){
        return Arrays.asList(longitude, latitude);
    }
}
