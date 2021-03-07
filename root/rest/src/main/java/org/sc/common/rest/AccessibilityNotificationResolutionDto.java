package org.sc.common.rest;


import java.util.Date;
import java.util.Objects;

public class AccessibilityNotificationResolutionDto {
    private String id;
    private String resolution;
    private Date resolutionDate;

    public AccessibilityNotificationResolutionDto() {
    }

    public AccessibilityNotificationResolutionDto(final String id,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessibilityNotificationResolutionDto that = (AccessibilityNotificationResolutionDto) o;
        return getId().equals(that.getId()) &&
                getResolution().equals(that.getResolution()) &&
                getResolutionDate().equals(that.getResolutionDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getResolution(), getResolutionDate());
    }
}