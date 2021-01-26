package org.sc.common.rest;

import java.util.*;

public class PoiDto {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<String> mediaIds;
    private List<String> trailIds;
    private TrailCoordinatesDto trailCoordinates;
    private Date createdOn;
    private Date lastUpdatedOn;
    private List<String> externalResources;

    public PoiDto() {
    }

    public PoiDto(String id, String name, String description, List<String> tags,
                  PoiMacroType macroType, List<String> microType,
                  List<String> mediaIds, List<String> trailIds,
                  TrailCoordinatesDto trailCoordinates, Date createdOn,
                  Date lastUpdatedOn, List<String> externalResources) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.macroType = macroType;
        this.microType = microType;
        this.mediaIds = mediaIds;
        this.trailIds = trailIds;
        this.trailCoordinates = trailCoordinates;
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

    public TrailCoordinatesDto getTrailCoordinates() {
        return trailCoordinates;
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

    public void setTrailCoordinates(TrailCoordinatesDto trailCoordinates) {
        this.trailCoordinates = trailCoordinates;
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
}