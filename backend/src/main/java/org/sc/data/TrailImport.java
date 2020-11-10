package org.sc.data;

import org.sc.common.rest.controller.CoordinatesWithAltitude;
import org.sc.common.rest.controller.Position;
import org.sc.common.rest.controller.TrailClassification;

import java.util.Date;
import java.util.List;

public class TrailImport {

    private final String name;
    private final String description;
    private final String code;
    private final Position startPos;
    private final Position finalPos;
    private final List<CoordinatesWithAltitude> coordinates;
    private final TrailClassification classification;
    private final String country;
    private final Date date;
    private final String maintainingSection;

    public TrailImport(final String name,
                       final String description,
                       final String code,
                       final Position startPos,
                       final Position finalPos,
                       final TrailClassification classification,
                       final String country,
                       final List<CoordinatesWithAltitude> coordinates,
                       final Date date, String maintainingSection) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.classification = classification;
        this.country = country;
        this.coordinates = coordinates;
        this.date = date;
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

    public List<CoordinatesWithAltitude> getCoordinates() {
        return coordinates;
    }

    public Date getDate() {
        return date;
    }

    public String getMaintainingSection() {
        return maintainingSection;
    }

}
