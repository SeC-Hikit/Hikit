package org.sc.common.rest;

import java.util.Date;

public class MaintenanceDto {
    private final Date date;
    private final String code;
    private final String meetingPlace;
    private final String description;
    private final String contact;

    public MaintenanceDto(Date date, String code, String meetingPlace,
                          String description, String contact) {
        this.date = date;
        this.code = code;
        this.meetingPlace = meetingPlace;
        this.description = description;
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }
}