package org.sc.common.rest;

import java.util.Date;
import java.util.Objects;

public class MaintenanceCreationDto {
    private Date date;
    private String code;
    private String meetingPlace;
    private String description;
    private String contact;

    public MaintenanceCreationDto() { }

    public MaintenanceCreationDto(Date date, String code, String meetingPlace,
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

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceDto that = (MaintenanceDto) o;
        return getDate().equals(that.getDate()) &&
                getCode().equals(that.getCode()) &&
                getMeetingPlace().equals(that.getMeetingPlace()) &&
                getDescription().equals(that.getDescription()) &&
                getContact().equals(that.getContact());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getCode(), getMeetingPlace(), getDescription(), getContact());
    }

}
