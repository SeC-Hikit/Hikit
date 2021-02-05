package org.sc.common.rest;


import java.util.Date;
import java.util.Objects;

public class AccessibilityUnresolvedDto {
    private String id;
    private String description;
    private String code;
    private Date reportDate;
    private boolean minor;
    private CoordinatesDto coordinates;

    public AccessibilityUnresolvedDto() {
    }

    public AccessibilityUnresolvedDto(String id,
                                      String description,
                                      String code,
                                      Date reportDate,
                                      boolean minor,
                                      CoordinatesDto coordinates) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.minor = minor;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public boolean isMinor() {
        return minor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public void setMinor(boolean minor) {
        this.minor = minor;
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
        AccessibilityUnresolvedDto that = (AccessibilityUnresolvedDto) o;
        return isMinor() == that.isMinor() &&
                getId().equals(that.getId()) &&
                getDescription().equals(that.getDescription()) &&
                getCode().equals(that.getCode()) &&
                getReportDate().equals(that.getReportDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescription(), getCode(), getReportDate(), isMinor());
    }
}