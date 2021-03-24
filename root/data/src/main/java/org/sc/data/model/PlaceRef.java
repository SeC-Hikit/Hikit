package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlaceRef {

    public static final String NAME = "name";
    public static final String COORDINATES = "coordinates";
    public static final String PLACE_ID = "placeId";

    private String name;
    private TrailCoordinates trailCoordinates;
    private String placeId;
}
