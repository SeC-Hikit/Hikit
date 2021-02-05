package org.sc.data.entity;

import org.sc.common.rest.PoiMacroType;

import java.util.Date;
import java.util.List;

public class Poi {
    public static final String OBJECT_ID = "_id";
    public static final String COLLECTION_NAME = "core.Poi";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";
    public static final String MACROTYPE = "macrotype";
    public static final String MICROTYPES = "microtypes";
    public static final String MEDIA_IDS = "mediaIds";
    public static final String TRAIL_CODES = "trailIds";
    public static final String TRAIL_COORDINATES = "trailCoordinates";
    public static final String CREATED_ON = "createdOn";
    public static final String LAST_UPDATE_ON = "lastUpdateOn";
    public static final String EXTERNAL_RESOURCES = "externalResources";

    private final String _id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<String> mediaIds;
    private List<String> trailIds;
    private CoordinatesWithAltitude coordinates;
    private Date createdOn;
    private Date lastUpdatedOn;
    private List<String> externalResources;

    public Poi(String _id, String name, String description, List<String> tags, PoiMacroType macroType, List<String> microType, List<String> mediaIds,
               List<String> trailIds, CoordinatesWithAltitude coordinates, Date createdOn, Date lastUpdatedOn, List<String> externalResources) {
        this._id = _id;
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

    public String get_id() {
        return _id;
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

    public CoordinatesWithAltitude getCoordinates() {
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

    public void setCoordinates(CoordinatesWithAltitude coordinates) {
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
}
