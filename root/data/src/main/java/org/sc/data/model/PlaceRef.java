package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PlaceRef {

    public static final String NAME = "name";
    public static final String COORDINATES = "coordinates";
    public static final String PLACE_ID = "placeId";
    public static final String ENCOUNTERED_TRAIL_IDS = "encounteredTrailIds";
    public static final String IS_DYNAMIC = "isDynamic";

    private String name;
    private CoordinatesWithAltitude coordinates;
    private String placeId;
    private List<String> encounteredTrailIds;
    private boolean dynamicCrossway;
}
