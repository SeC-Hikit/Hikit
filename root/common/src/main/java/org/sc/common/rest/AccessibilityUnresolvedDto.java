package org.sc.common.rest;


import java.util.Date;

public class AccessibilityUnresolvedDto {
    private String id;
    private String description;
    private String code;
    private Date reportDate;
    private boolean isMinor;

    public AccessibilityUnresolvedDto() {
    }

    public AccessibilityUnresolvedDto(String id,
                                      String description,
                                      String code,
                                      Date reportDate,
                                      boolean isMinor) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.isMinor = isMinor;
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
        return isMinor;
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
        isMinor = minor;
    }
}