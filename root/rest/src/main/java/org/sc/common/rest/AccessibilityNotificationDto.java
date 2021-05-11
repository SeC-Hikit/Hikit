package org.sc.common.rest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessibilityNotificationDto {
    private String id;
    private String description;
    private String trailId;
    private Date reportDate;
    private Date resolutionDate;
    private boolean minor;
    private String resolution;
    private CoordinatesDto coordinates;
    private RecordDetailsDto recordDetails;
}

