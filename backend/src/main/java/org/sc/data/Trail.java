package org.sc.data;

import java.util.Date;
import java.util.List;

public class Trail {

    public static final String COLLECTION_NAME = "core.Trail";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CODE = "code";
    public static final String START_POS = "startPos";
    public static final String FINAL_POS = "finalPos";
    public static final String TRACK_LENGTH = "trackLength";
    public static final String ETA = "eta";
    public static final String CLASSIFICATION = "classification";
    public static final String COUNTRY = "country";
    public static final String COORDINATES = "coordinates";
    public static final String LAST_UPDATE_DATE = "lastUpdate";
    public static final String SECTION_CARED_BY = "maintainingSection";

    private String name;
    private String description;
    private String code;
    private Position startPos;
    private Position finalPos;
    private List<CoordinatesWithAltitude> coordinates;
    private double trackLength;
    private double eta;
    private TrailClassification classification;
    private final String country;
    private final Date date;
    private final String maintainingSection;

    public Trail(final String name,
                 final String description,
                 final String code,
                 final Position startPos,
                 final Position finalPos,
                 final double trackLength,
                 final double eta,
                 final TrailClassification classification,
                 final String country,
                 final List<CoordinatesWithAltitude> coordinates,
                 final Date date, String maintainingSection) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.trackLength = trackLength;
        this.eta = eta;
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

    public double getTrackLength() {
        return trackLength;
    }

    public double getEta() {
        return eta;
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

    public static final class TrailBuilder {
        private String name;
        private String description;
        private String code;
        private Position startPos;
        private Position finalPos;
        private List<CoordinatesWithAltitude> coordinates;
        private double trackLength;
        private double eta;
        private TrailClassification trailClassification;
        private String country;
        private Date date;
        private String maintainingSection;

        private TrailBuilder() {
        }

        public static TrailBuilder aTrail() {
            return new TrailBuilder();
        }

        public TrailBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TrailBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public TrailBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public TrailBuilder withStartPos(Position startPos) {
            this.startPos = startPos;
            return this;
        }

        public TrailBuilder withFinalPos(Position finalPos) {
            this.finalPos = finalPos;
            return this;
        }

        public TrailBuilder withCoordinates(List<CoordinatesWithAltitude> coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public TrailBuilder withTrackLength(double trackLength) {
            this.trackLength = trackLength;
            return this;
        }

        public TrailBuilder withEta(double eta) {
            this.eta = eta;
            return this;
        }

        public TrailBuilder withTrailClassification(TrailClassification trailClassification) {
            this.trailClassification = trailClassification;
            return this;
        }

        public TrailBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public TrailBuilder withDate(Date date) {
            this.date = date;
            return this;
        }

        public TrailBuilder withMaintainingSection(String maintainingSection) {
            this.maintainingSection = maintainingSection;
            return this;
        }

        public Trail build() {
            return new Trail(name, description, code, startPos, finalPos, trackLength, eta, trailClassification, country, coordinates, date, maintainingSection);
        }
    }
}
