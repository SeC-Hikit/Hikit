package org.sc.common.rest;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TrailDto {

    private String code;
    private String name;
    private String description;
    private PositionDto startPos;
    private PositionDto finalPos;
    private List<PositionDto> locations;
    private TrailClassification classification;
    private String country;
    private StatsTrailMetadataDto statsMetadata;
    private List<TrailCoordinatesDto> coordinates;
    private List<LinkedMediaDto> mediaList;
    private Date lastUpdate;
    private String maintainingSection;

    public TrailDto() {
    }

    public TrailDto(String code, String name, String description, PositionDto startPos,
                    PositionDto finalPos, List<PositionDto> locations, TrailClassification classification, String country,
                    StatsTrailMetadataDto statsMetadata, List<TrailCoordinatesDto> coordinates,
                    Date lastUpdate, String maintainingSection, List<LinkedMediaDto> mediaList) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.locations = locations;
        this.classification = classification;
        this.country = country;
        this.statsMetadata = statsMetadata;
        this.coordinates = coordinates;
        this.lastUpdate = lastUpdate;
        this.maintainingSection = maintainingSection;
        this.mediaList = mediaList;
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

    public PositionDto getFinalPos() {
        return finalPos;
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

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartPos(PositionDto startPos) {
        this.startPos = startPos;
    }

    public void setFinalPos(PositionDto finalPos) {
        this.finalPos = finalPos;
    }

    public void setLocations(List<PositionDto> locations) {
        this.locations = locations;
    }

    public void setClassification(TrailClassification classification) {
        this.classification = classification;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setStatsMetadata(StatsTrailMetadataDto statsMetadata) {
        this.statsMetadata = statsMetadata;
    }

    public void setCoordinates(List<TrailCoordinatesDto> coordinates) {
        this.coordinates = coordinates;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setMaintainingSection(String maintainingSection) {
        this.maintainingSection = maintainingSection;
    }


    public List<LinkedMediaDto> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<LinkedMediaDto> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrailDto trailDto = (TrailDto) o;
        return getCode().equals(trailDto.getCode()) && getName().equals(trailDto.getName()) && getDescription().equals(trailDto.getDescription()) && getStartPos().equals(trailDto.getStartPos()) && getFinalPos().equals(trailDto.getFinalPos()) && getLocations().equals(trailDto.getLocations()) && getClassification() == trailDto.getClassification() && getCountry().equals(trailDto.getCountry()) && getStatsMetadata().equals(trailDto.getStatsMetadata()) && getCoordinates().equals(trailDto.getCoordinates()) && getMediaList().equals(trailDto.getMediaList()) && getLastUpdate().equals(trailDto.getLastUpdate()) && getMaintainingSection().equals(trailDto.getMaintainingSection());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(),
                getName(), getDescription(), getStartPos(), getFinalPos(),
                getLocations(), getClassification(), getCountry(), getStatsMetadata(),
                getCoordinates(), getLastUpdate(), getMaintainingSection());
    }
}