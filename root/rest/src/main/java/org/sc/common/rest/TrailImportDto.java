package org.sc.common.rest;

import lombok.Data;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;

import java.util.Date;
import java.util.List;

@Data
public class TrailImportDto {
    private final String code;
    private final String name;
    private final String description;
    private final int officialEta;
    private final PlaceRefDto startLocation;
    private final PlaceRefDto endLocation;
    private final List<PlaceRefDto> locations;
    private final List<PlaceRefDto> crossways;
    private final TrailClassification classification;
    private final String country;
    private final List<TrailCoordinatesDto> coordinates;
    private final String maintainingSection;
    private final boolean isVariant;
    private final String territorialDivision;
    private final List<LinkedMediaDto> linkedMediaDtos;
    private final Date lastUpdate;
    private final FileDetailsDto fileDetailsDto;
    private final TrailStatus trailStatus;
}