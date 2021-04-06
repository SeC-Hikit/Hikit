package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class Maintenance {

    public static final String COLLECTION_NAME = "core.Maintenance";

    public static final String OBJECT_ID = "_id";
    public static final String TRAIL_ID = "trailId";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String CONTACT = "contact";
    public static final String MEETING_PLACE = "meetingPlace";

    private String _id;
    private Date date;
    private String trailId;
    private String meetingPlace;
    private String description;
    private String contact;
}