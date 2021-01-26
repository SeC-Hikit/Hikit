package org.sc.common.rest;


import java.util.Date;

public class AccessibilityNotificationDto {
    private String id;
    private String description;
    private String code;
    private Date reportDate;
    private Date resolutionDate;
    private boolean isMinor;
    private String resolution;

    public AccessibilityNotificationDto() {
    }

    public AccessibilityNotificationDto(String id,
                                        String description,
                                        String code,
                                        Date reportDate,
                                        Date resolutionDate,
                                        boolean isMinor,
                                        String resolution) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.resolutionDate = resolutionDate;
        this.isMinor = isMinor;
        this.resolution = resolution;
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

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public boolean isMinor() {
        return isMinor;
    }

    public String getResolution() {
        return resolution;
    }

    public String getId() {
        return id;
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

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public void setMinor(boolean minor) {
        isMinor = minor;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}

