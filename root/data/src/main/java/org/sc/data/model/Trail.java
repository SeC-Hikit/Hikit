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
    public static final String GEO_LINE = "geoLine";
    
    public static final String MEDIA = "media";

    private String id;
    private String name;
    private String description;
    private String code;
    private boolean variant;
    private int officialEta;
    private List<PlaceRef> locations;
    private List<TrailCoordinates> coordinates;
    private TrailClassification classification;
    private String country;
    private Date lastUpdate;
    private String maintainingSection;
    private Date createdOn;
    private String territorialDivision;
    private StatsTrailMetadata statsTrailMetadata;
    private GeoLineString geoLineString;
    private List<LinkedMedia> mediaList;

    // TODO:
    // status: solo_file, draft, public
    // file: { uploadedOn: , uploadedBy: , fileName: }
    // lastModified: { user: , date: }
    // bike:
    //    Difficolta' prevalente (TC, TC+, MC, MC+, BC, BC+, OC, OC+, non percorribile in bici)
    //    Official ETA (tempo di percorrenza)
    //    Percorribilità in bici da A a B : yes/no
    //    Indicazione per pezzi di portage: 15 / 20 / >30 min
    //    Percorribilità in bici da B a A -> (si/no)
    //    Indicazione per pezzi di portage: 15 / 20 / >30 min
    //            Note

}

