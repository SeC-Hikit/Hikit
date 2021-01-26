package org.sc.data.entity;

import java.util.*;

public class AccessibilityUnresolved {
    private final String _id;
    private String description;
    private String code;
    private Date reportDate;
    private boolean isMinor;

    public AccessibilityUnresolved(String _id, String description, String code, Date reportDate, boolean isMinor) {
        this._id = _id;
        this.description = description;
        this.code = code;
        this.reportDate = reportDate;
        this.isMinor = isMinor;
    }

    public String get_id() {
        return _id;
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