package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    private Set<String> sortKeys = Set.of("reportDate");
}
