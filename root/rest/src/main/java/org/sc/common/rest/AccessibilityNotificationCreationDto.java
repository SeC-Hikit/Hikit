package org.sc.common.rest;

import java.util.Date;
import java.util.Objects;

public class AccessibilityNotificationCreationDto {
    private String code;
    private String description;
    private Date reportDate;
    private boolean isMinor;
    private CoordinatesDto coordinates;

    public AccessibilityNotificationCreationDto() {
    }

    public AccessibilityNotificationCreationDto(String code,
                                                String description,
                                                Date reportDate,
                                                boolean minor,
                                                CoordinatesDto coordinates) {
        this.code = code;
        this.description = description;
        this.reportDate = reportDate;
        this.isMinor = minor;
        this.coordinates = coordinates;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public boolean isMinor() {
        return isMinor;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public void setMinor(boolean minor) {
        isMinor = minor;
    }

    public CoordinatesDto getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessibilityNotificationCreationDto that = (AccessibilityNotificationCreationDto) o;
        return isMinor() == that.isMinor() &&
                getCode().equals(that.getCode()) &&
                getDescription().equals(that.getDescription()) &&
                getReportDate().equals(that.getReportDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getDescription(), getReportDate(), isMinor());
    }
}