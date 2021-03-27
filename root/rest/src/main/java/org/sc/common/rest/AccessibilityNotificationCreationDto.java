package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
public class AccessibilityNotificationCreationDto {
    private String trailId;
    private String description;
    private Date reportDate;
    private boolean isMinor;
    private CoordinatesDto coordinates;
}