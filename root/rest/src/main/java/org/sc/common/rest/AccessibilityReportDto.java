package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AccessibilityReportDto {
    private String id;
    private String description;
    private String trailId;
    private String email;
    private String telephone;
    private String issueId;
    private Date reportDate;
    private Boolean valid;
    private CoordinatesDto coordinates;
    private RecordDetailsDto recordDetails;
}
