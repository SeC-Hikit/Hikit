package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Poi {
    public static final String COLLECTION_NAME = "core.Poi";

    public static final String OBJECT_ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";
    public static final String MACROTYPE = "macrotype";
    public static final String MICROTYPES = "microtypes";
    public static final String MEDIA = "media";
    public static final String TRAIL_CODES = "trailIds";
    public static final String TRAIL_COORDINATES = "trailCoordinates";
    public static final String CREATED_ON = "createdOn";
    public static final String LAST_UPDATE_ON = "lastUpdateOn";
    public static final String EXTERNAL_RESOURCES = "externalResources";
    public static final String KEY_VAL = "kvps";
    public static final String RECORD_DETAILS = "recordDetails";

    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<LinkedMedia> mediaList;
    private List<String> trailIds;
    private CoordinatesWithAltitude coordinates;
    private Date createdOn;
    private Date lastUpdatedOn;
    private List<String> externalResources;
    private List<KeyVal> keyVal;
    private RecordDetails recordDetails;

}
