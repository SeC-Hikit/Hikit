package org.sc.common.rest;


import java.util.Date;

public class AccessibilityNotificationResolutionDto {
    private String id;
    private String resolution;
    private Date resolutionDate;

    AccessibilityNotificationResolutionDto(final String id,
                                           final String resolution,
                                           final Date resolutionDate) {
        this.id = id;
        this.resolution = resolution;
        this.resolutionDate = resolutionDate;
    }

    public String getId() {
        return id;
    }

    public String getResolution() {
        return resolution;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }
}