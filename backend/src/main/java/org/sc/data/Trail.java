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
    public static final String POST_CODE = "postCodes";
    public static final String COUNTRY = "country";
    public static final String GEO_POINTS = "geoPoints";
    public static final String LAST_UPDATE = "lastUpdate";

    private String name;
    private String description;
    private String code;
    private Position startPos;
    private Position finalPos;
    private List<CoordinatesWithAltitude> coordinates;
    private double trackLength;
    private double eta;
    private TrailClassification trailClassification;
    private final List<String> postCode;
    private final String country;
    private final Date lastUpdate;

    public Trail(String name, String description, String code, Position startPos, Position finalPos, double trackLength,
                 double eta, TrailClassification trailClassification, List<String> postCode, String country,
                 List<CoordinatesWithAltitude> coordinates, Date lastUpdate) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.trackLength = trackLength;
        this.eta = eta;
        this.trailClassification = trailClassification;
        this.postCode = postCode;
        this.country = country;
        this.coordinates = coordinates;
        this.lastUpdate = lastUpdate;
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

    public TrailClassification getTrailClassification() {
        return trailClassification;
    }

    public Position getFinalPos() {
        return finalPos;
    }

    public List<String> getPostCodes() {
        return postCode;
    }

    public String getCountry() {
        return country;
    }

    public List<CoordinatesWithAltitude> getCoordinates() {
        return coordinates;
    }

    public static final class TrailBuilder {

        private String name;
        private String description;
        private String code;
        private List<String> postCode;
        private Position startPos;
        private Position finalPos;
        private double trackLength;
        private double eta;
        private TrailClassification trailClassification;
        private String country;
        private List<CoordinatesWithAltitude> coordinates;
        private Date lastUpdate;

        private TrailBuilder() {
        }

        public static TrailBuilder aTrail() {
            return new TrailBuilder();
        }

        public TrailBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TrailBuilder withPostCodes(List<String> postCode) {
            this.postCode = postCode;
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

        public TrailBuilder withTrackLength(double trackLength) {
            this.trackLength = trackLength;
            return this;
        }

        public TrailBuilder withEta(double eta) {
            this.eta = eta;
            return this;
        }

        public TrailBuilder withClassification(TrailClassification trailClassification) {
            this.trailClassification = trailClassification;
            return this;
        }

        public TrailBuilder withFinalPos(Position finalPos) {
            this.finalPos = finalPos;
            return this;
        }

        public TrailBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public TrailBuilder withCoordinates(List<CoordinatesWithAltitude> coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public TrailBuilder withLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }


        public Trail build() {
            return new Trail(name, description, code, startPos, finalPos,
                    trackLength, eta, trailClassification,
                    postCode, country, coordinates, lastUpdate);
        }

    }
}
