package org.sc.common.rest;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
public class AccessibilityNotificationResolutionDto {
    private String id;
    private String resolution;
    private Date resolutionDate;
}