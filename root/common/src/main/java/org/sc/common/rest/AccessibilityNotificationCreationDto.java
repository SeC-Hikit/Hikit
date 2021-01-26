package org.sc.common.rest;

import java.util.Date;

public class AccessibilityNotificationCreationDto {
    private String code;
    private String description;
    private Date reportDate;
    private boolean isMinor;

    public AccessibilityNotificationCreationDto(String code,
                                                String description,
                                                Date reportDate,
                                                boolean isMinor) {
        this.code = code;
        this.description = description;
        this.reportDate = reportDate;
        this.isMinor = isMinor;
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
}