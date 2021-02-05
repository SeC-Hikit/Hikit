package org.sc.common.rest;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PoiDto {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<String> mediaIds;
    private List<String> trailIds;
    private CoordinatesDto coordinates;
    private Date createdOn;
    private Date lastUpdatedOn;
    private List<String> externalResources;

    public PoiDto() {
    }

    public PoiDto(String id, String name, String description, List<String> tags,
                  PoiMacroType macroType, List<String> microType,
                  List<String> mediaIds, List<String> trailIds,
                  CoordinatesDto coordinates, Date createdOn,
                  Date lastUpdatedOn, List<String> externalResources) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.macroType = macroType;
        this.microType = microType;
        this.mediaIds = mediaIds;
        this.trailIds = trailIds;
        this.coordinates = coordinates;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.externalResources = externalResources;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public PoiMacroType getMacroType() {
        return macroType;
    }

    public List<String> getMicroType() {
        return microType;
    }

    public List<String> getMediaIds() {
        return mediaIds;
    }

    public List<String> getTrailIds() {
        return trailIds;
    }

    public CoordinatesDto getCoordinates() {
        return coordinates;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public List<String> getExternalResources() {
        return externalResources;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setMacroType(PoiMacroType macroType) {
        this.macroType = macroType;
    }

    public void setMicroType(List<String> microType) {
        this.microType = microType;
    }

    public void setMediaIds(List<String> mediaIds) {
        this.mediaIds = mediaIds;
    }

    public void setTrailIds(List<String> trailIds) {
        this.trailIds = trailIds;
    }

    public void setCoordinates(CoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public void setExternalResources(List<String> externalResources) {
        this.externalResources = externalResources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiDto poiDto = (PoiDto) o;
        return getId().equals(poiDto.getId()) &&
                getName().equals(poiDto.getName()) &&
                getDescription().equals(poiDto.getDescription()) &&
                getTags().equals(poiDto.getTags()) &&
                getMacroType() == poiDto.getMacroType() &&
                getMicroType().equals(poiDto.getMicroType()) &&
                getMediaIds().equals(poiDto.getMediaIds()) &&
                getTrailIds().equals(poiDto.getTrailIds()) &&
                getCoordinates().equals(poiDto.getCoordinates()) &&
                getCreatedOn().equals(poiDto.getCreatedOn()) &&
                getLastUpdatedOn().equals(poiDto.getLastUpdatedOn()) &&
                getExternalResources().equals(poiDto.getExternalResources());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getTags(), getMacroType(),
                getMicroType(), getMediaIds(), getTrailIds(), getCoordinates(), getCreatedOn(),
                getLastUpdatedOn(), getExternalResources());
    }
}