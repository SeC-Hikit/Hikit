package org.sc.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Coordinates2D {
    private final Double latitude;
    private final Double longitude;

    public Coordinates2D(final Double longitude,
                         final Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonIgnore
    public List<Double> getAsList(){
        return Arrays.asList(longitude, latitude);
    }
}
