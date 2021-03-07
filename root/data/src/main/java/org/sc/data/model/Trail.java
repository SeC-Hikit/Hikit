package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Trail {

    public static final String COLLECTION_NAME = "core.Trail";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CODE = "code";
    public static final String OFFICIAL_ETA = "officialEta";
    public static final String VARIANT = "variant";
    public static final String START_POS = "startPos";
    public static final String FINAL_POS = "finalPos";
    public static final String CLASSIFICATION = "classification";
    public static final String COUNTRY = "country";
    public static final String STATS_METADATA = "statsMetadata";
    public static final String COORDINATES = "coordinates";
    public static final String CREATED_ON_DATE = "lastUpdate";
    public static final String LAST_UPDATE_DATE = "lastUpdate";
    public static final String SECTION_CARED_BY = "maintainingSection";
    public static final String TERRITORIAL_CARED_BY = "territorialDivision";
    public static final String LOCATIONS = "locations";

    private String id;
    private String name;
    private String description;
    private String code;
    private boolean variant;
    private int officialEta;
    private Position startPos;
    private Position finalPos;
    private List<Position> locations;
    private List<TrailCoordinates> coordinates;
    private TrailClassification classification;
    private String country;
    private Date lastUpdate;
    private String maintainingSection;
    private Date createdOn;
    private String territorialDivision;
    private StatsTrailMetadata statsTrailMetadata;
}

