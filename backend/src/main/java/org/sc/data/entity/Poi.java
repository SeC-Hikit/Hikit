package org.sc.data.entity;

import org.sc.common.rest.PoiMacroType;

import java.util.Date;
import java.util.List;

public class Poi {

    public static final String COLLECTION_NAME = "core.Poi";

    public static final String OBJECT_ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";
    public static final String MACROTYPE = "macrotype";
    public static final String MICROTYPES = "microtypes";
    public static final String MEDIA = "media";
    public static final String TRAIL_CODES = "trailIds";
    public static final String TRAIL_COORDINATES = "trailCoordinates";
    public static final String CREATED_ON = "createdOn";
    public static final String LAST_UPDATE_ON = "lastUpdateOn";
    public static final String EXTERNAL_RESOURCES = "externalResources";
    public static final String KEY_VAL = "kvps";

    private final String _id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<LinkedMedia> mediaList;
    private List<String> trailIds;
    private CoordinatesWithAltitude coordinates;
    private Date createdOn;
    private Date lastUpdatedOn;
    private List<String> externalResources;
    private List<KeyVal> keyVal;

    public Poi(String _id, String name, String description, List<String> tags,
               PoiMacroType macroType, List<String> microType, List<LinkedMedia> mediaList,
               List<String> trailIds, CoordinatesWithAltitude coordinates, Date createdOn,
               Date lastUpdatedOn, List<String> externalResources, List<KeyVal> keyVal) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.macroType = macroType;
        this.microType = microType;
        this.mediaList = mediaList;
        this.trailIds = trailIds;
        this.coordinates = coordinates;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.externalResources = externalResources;
        this.keyVal = keyVal;
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

    public List<LinkedMedia> getMediaList() {
        return mediaList;
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

    public void setMediaList(List<LinkedMedia> mediaList) {
        this.mediaList = mediaList;
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

    public List<KeyVal> getKeyVal() {
        return keyVal;
    }

    public void setKeyVal(List<KeyVal> keyVal) {
        this.keyVal = keyVal;
    }
}
