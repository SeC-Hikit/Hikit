package org.sc.data.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class Place {

    public static final String COLLECTION_NAME = "core.Place";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";
    public static final String MEDIA_IDS = "mediaIds";
    public static final String POINTS = "points";
    public static final String COORDINATES = "coordinates";
    public static final String CROSSING = "crossingIds";
    public static final String RECORD_DETAILS = "recordDetails";
    public static final String IS_DYNAMIC_CROSSWAY = "isDynamic";

    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private List<String> mediaIds;
    private MultiPointCoords2D points;
    private List<CoordinatesWithAltitude> coordinates;
    private List<String> crossingTrailIds;
    private boolean isDynamic;
    private RecordDetails recordDetails;
}
