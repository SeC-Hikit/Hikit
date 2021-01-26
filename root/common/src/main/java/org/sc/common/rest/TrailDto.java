package org.sc.common.rest;

import java.util.*;

public class TrailDto {
    private String code;
    private String name;
    private String description;
    private PositionDto startPos;
    private PositionDto Pos;
    private List<PositionDto> locations;
    private TrailClassification classification;
    private String country;
    private StatsTrailMetadataDto statsMetadata;
    private List<TrailCoordinatesDto> coordinates;
    private Date lastUpdate;
    private String maintainingSection;

    public TrailDto() {
    }

    public TrailDto(String code, String name, String description, PositionDto startPos,
                    PositionDto Pos, List<PositionDto> locations, TrailClassification classification, String country,
                    StatsTrailMetadataDto statsMetadata, List<TrailCoordinatesDto> coordinates,
                    Date lastUpdate, String maintainingSection) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.startPos = startPos;
        this.Pos = Pos;
        this.locations = locations;
        this.classification = classification;
        this.country = country;
        this.statsMetadata = statsMetadata;
        this.coordinates = coordinates;
        this.lastUpdate = lastUpdate;
        this.maintainingSection = maintainingSection;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PositionDto getStartPos() {
        return startPos;
    }

    public PositionDto getPos() {
        return Pos;
    }

    public List<PositionDto> getLocations() {
        return locations;
    }

    public TrailClassification getClassification() {
        return classification;
    }

    public String getCountry() {
        return country;
    }

    public StatsTrailMetadataDto getStatsMetadata() {
        return statsMetadata;
    }

    public List<TrailCoordinatesDto> getCoordinates() {
        return coordinates;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getMaintainingSection() {
        return maintainingSection;
    }
}