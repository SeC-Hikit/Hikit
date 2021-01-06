package org.sc.data;

import org.sc.common.rest.Position;
import org.sc.common.rest.TrailClassification;
import org.sc.common.rest.TrailCoordinates;

import java.util.Date;
import java.util.List;

public class TrailImport {

    private final String name;
    private final String description;
    private final String code;
    private final Position startPos;
    private final Position finalPos;
    private final List<Position> locations;
    private final List<TrailCoordinates> coordinates;
    private final TrailClassification classification;
    private final String country;
    private final Date lastUpdate;
    private final String maintainingSection;

    public TrailImport(final String name,
                       final String description,
                       final String code,
                       final Position startPos,
                       final Position finalPos,
                       final List<Position> locations,
                       final TrailClassification classification,
                       final String country,
                       final List<TrailCoordinates> coordinates,
                       final Date lastUpdate, String maintainingSection) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.locations = locations;
        this.classification = classification;
        this.country = country;
        this.coordinates = coordinates;
        this.lastUpdate = lastUpdate;
        this.maintainingSection = maintainingSection;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public Position getStartPos() {
        return startPos;
    }

    public TrailClassification getClassification() {
        return classification;
    }

    public Position getFinalPos() {
        return finalPos;
    }

    public String getCountry() {
        return country;
    }

    public List<TrailCoordinates> getCoordinates() {
        return coordinates;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getMaintainingSection() {
        return maintainingSection;
    }

    public List<Position> getLocations() {
        return locations;
    }
}
