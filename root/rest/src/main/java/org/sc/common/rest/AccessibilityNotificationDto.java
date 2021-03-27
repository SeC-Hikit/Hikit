package org.sc.common.rest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
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
}

