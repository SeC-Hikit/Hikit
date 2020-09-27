package org.sc.data;

import org.bson.types.ObjectId;

import java.util.Date;

public class AccessibilityNotification {

    public static final String COLLECTION_NAME = "core.AccessibilityNotifications";

    public static final String OBJECT_ID = "_id";
    public static final String TRAIL_CODE = "code";
    public static final String DESCRIPTION = "description";
    public static final String REPORT_DATE = "reportDate";
    public static final String RESOLUTION_DATE = "resolutionDate";
    public static final String IS_MINOR = "isMinor";

    private ObjectId _id;
    private String description;
    private String code;
    private Date reportDate;
    private Date resolutionDate;
    private boolean isMinor;
    private final String resolution;

    public AccessibilityNotification(String code, String description, Date reportDate, boolean isMinor, String resolution) {
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.isMinor = isMinor;
        this.resolution = resolution;
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

    public boolean isMinor() {
        return isMinor;
    }

    public ObjectId getId() {
        return _id;
    }
}
