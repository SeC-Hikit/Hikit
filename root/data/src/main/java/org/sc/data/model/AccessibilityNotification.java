package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class AccessibilityNotification {
    public final static String COLLECTION_NAME = "core.AccessibilityNotifications";

    public final static String ID = "_id";
    public final static String TRAIL_ID = "trailId";
    public final static String DESCRIPTION = "description";
    public final static String REPORT_DATE = "reportDate";
    public final static String RESOLUTION_DATE = "resolutionDate";
    public final static String IS_MINOR = "isMinor";
    public final static String RESOLUTION = "resolution";
    public final static String COORDINATES = "coordinates";
    public final static String RECORD_DETAILS = "recordDetails";

    private final String _id;
    private String description;
    private String trailId;
    private Date reportDate;
    private Date resolutionDate;
    private boolean minor;
    private CoordinatesWithAltitude coordinates;
    private String resolution;
    private RecordDetails recordDetails;
}
