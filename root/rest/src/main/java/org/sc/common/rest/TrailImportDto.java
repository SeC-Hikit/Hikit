package org.sc.common.rest;

import lombok.Data;
import org.sc.data.model.TrailClassification;

import java.util.Date;
import java.util.List;

@Data
public class TrailImportDto {
    private final String code;
    private final String name;
    private final String description;
    private final int officialEta;
    private final List<PlaceRefDto> locations;
    private final TrailClassification classification;
    private final String country;
    private final List<TrailCoordinatesDto> coordinates;
    private final Date lastUpdate;
    private final String maintainingSection;
    private final boolean isVariant;
    private final String territorialDivision;
    private final Date createdOn;
}