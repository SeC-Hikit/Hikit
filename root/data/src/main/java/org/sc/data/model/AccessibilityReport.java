package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityReport {

    public final static String COLLECTION_NAME = "core.AccessibilityReports";

    public final static String ID = "_id";
    public final static String TRAIL_ID = "trailId";
    public final static String EMAIL = "email";
    public final static String TELEPHONE = "telephone";
    public final static String DESCRIPTION = "description";
    public final static String REPORT_DATE = "reportDate";
    public final static String ISSUE_ID = "issuedId";
    public final static String VALIDATION_ID = "validationId";
    public final static String IS_VALID = "isValid";
    public final static String COORDINATES = "coordinates";
    public final static String RECORD_DETAILS = "recordDetails";

    private String id;
    private String description;
    private String trailId;
    private String email;
    private String telephone;
    private Date reportDate;
    private String issueId;
    private boolean valid;
    private TrailCoordinates coordinates;
    private RecordDetails recordDetails;
}
