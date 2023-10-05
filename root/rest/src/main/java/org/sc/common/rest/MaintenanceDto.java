package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class MaintenanceDto {
    private String id;
    private Date date;
    private String trailId;
    private String trailCode;
    private String meetingPlace;
    private String description;
    private String contact;
    private RecordDetailsDto recordDetails;
}