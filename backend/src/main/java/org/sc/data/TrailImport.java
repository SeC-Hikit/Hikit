package org.sc.data;

import org.sc.common.rest.PositionDto;
import org.sc.data.model.TrailClassification;
import org.sc.common.rest.TrailCoordinatesDto;

import java.util.Date;
import java.util.List;

public class TrailImport {

    private final String name;
    private final String description;
    private final String code;
    private final PositionDto startPos;
    private final PositionDto finalPos;
    private final List<PositionDto> locations;
    private final List<TrailCoordinatesDto> coordinates;
    private final TrailClassification classification;
    private final String country;
    private final Date lastUpdate;
    private final String maintainingSection;

    public TrailImport(final String name,
                       final String description,
                       final String code,
                       final PositionDto startPos,
                       final PositionDto finalPos,
                       final List<PositionDto> locations,
                       final TrailClassification classification,
                       final String country,
                       final List<TrailCoordinatesDto> coordinates,
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

    public PositionDto getStartPos() {
        return startPos;
    }

    public TrailClassification getClassification() {
        return classification;
    }

    public PositionDto getFinalPos() {
        return finalPos;
    }

    public String getCountry() {
        return country;
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

    public List<PositionDto> getLocations() {
        return locations;
    }
}
