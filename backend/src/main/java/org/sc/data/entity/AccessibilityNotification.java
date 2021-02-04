package org.sc.data.entity;

import java.util.Date;

public class AccessibilityNotification {
    public final static String COLLECTION_NAME = "core.AccessibilityNotifications";

    public final static String OBJECT_ID = "_id";
    public final static String TRAIL_CODE = "code";
    public final static String DESCRIPTION = "description";
    public final static String REPORT_DATE = "reportDate";
    public final static String RESOLUTION_DATE = "resolutionDate";
    public final static String IS_MINOR = "isMinor";
    public final static String RESOLUTION = "resolution";
    public final static String COORDINATES = "coordinates";


    private final String _id;
    private String description;
    private String code;
    private Date reportDate;
    private Date resolutionDate;
    private boolean minor;
    private CoordinatesWithAltitude coordinates;
    private String resolution;


    public AccessibilityNotification(String _id,
                                     String description,
                                     String code,
                                     Date reportDate,
                                     Date resolutionDate,
                                     boolean minor,
                                     CoordinatesWithAltitude coordinates,
                                     String resolution) {
        this._id = _id;
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.resolutionDate = resolutionDate;
        this.minor = minor;
        this.coordinates = coordinates;
        this.resolution = resolution;
    }

    public String get_id() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public CoordinatesWithAltitude getCoordinates() {
        return coordinates;
    }

    public boolean isMinor() {
        return minor;
    }

    public String getResolution() {
        return resolution;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public void setMinor(boolean minor) {
        this.minor = minor;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setCoordinates(CoordinatesWithAltitude coordinates) {
        this.coordinates = coordinates;
    }
}
