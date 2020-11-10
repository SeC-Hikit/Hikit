package org.sc.common.rest.controller;

import java.util.Date;
import java.util.List;

public class Trail {

    public static final String COLLECTION_NAME = "core.Trail";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CODE = "code";
    public static final String START_POS = "startPos";
    public static final String FINAL_POS = "finalPos";
    public static final String CLASSIFICATION = "classification";
    public static final String COUNTRY = "country";
    public static final String STATS_METADATA = "statsMetadata";
    public static final String COORDINATES = "coordinates";
    public static final String LAST_UPDATE_DATE = "lastUpdate";
    public static final String SECTION_CARED_BY = "maintainingSection";

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
    private StatsTrailMetadata statsMetadata;

    public Trail(final String name,
                 final String description,
                 final String code,
                 final Position startPos,
                 final Position finalPos,
                 final TrailClassification classification,
                 final String country,
                 final StatsTrailMetadata statsMetadata,
                 final List<CoordinatesWithAltitude> coordinates,
                 final Date date, String maintainingSection) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.classification = classification;
        this.country = country;
        this.statsMetadata = statsMetadata;
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

    public StatsTrailMetadata getStatsMetadata() {
        return statsMetadata;
    }

    public void setStatsMetadata(StatsTrailMetadata statsMetadata) {
        this.statsMetadata = statsMetadata;
    }

    public static final class TrailBuilder {
        private String name;
        private String description;
        private String code;
        private Position startPos;
        private Position finalPos;
        private StatsTrailMetadata statsTrailMetadata;
        private List<CoordinatesWithAltitude> coordinates;
        private TrailClassification classification;
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

        public TrailBuilder withTrailMetadata(StatsTrailMetadata statsTrailMetadata) {
            this.statsTrailMetadata = statsTrailMetadata;
            return this;
        }

        public TrailBuilder withCoordinates(List<CoordinatesWithAltitude> coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public TrailBuilder withClassification(TrailClassification classification) {
            this.classification = classification;
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
            return new Trail(name, description, code, startPos, finalPos, classification,
                    country, statsTrailMetadata, coordinates, date, maintainingSection);
        }
    }
}
