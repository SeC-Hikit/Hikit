package org.sc.data;

import org.bson.types.ObjectId;

import java.util.Date;

public class Maintenance {

    public static final String COLLECTION_NAME = "core.Maintenance";

    public static final String TRAIL_CODE = "code";
    public static final String REPORT_DATE = "reportDate";
    public static final String DESCRIPTION = "description";
    public static final String RESOLUTION_DATE = "resolutionDate";
    public static final String IS_MINOR = "isMinor";

    private ObjectId _id;
    private Date date;
    private String code;
    private String meetingPlace;
    private String description;
    private String trailId;
    private String contact;

    public Maintenance(ObjectId id, Date date, String code, String meetingPlace, String description, String trailId, String contact) {
        _id = id;
        this.date = date;
        this.code = code;
        this.meetingPlace = meetingPlace;
        this.description = description;
        this.trailId = trailId;
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public String getDescription() {
        return description;
    }

    public String getTrailId() {
        return trailId;
    }

    public String getContact() {
        return contact;
    }

    public ObjectId getId() {
        return _id;
    }

}
