package org.sc.common.rest.controller;


import java.util.List;

public class Position {

    public static final String NAME = "name";
    public static final String TAGS = "tags";
    public static final String COORDINATES = "coordinates";

    private String name;
    private List<String> tags;
    private CoordinatesWithAltitude coordinates;

    public Position(double alt, double lat, double longitude) {
        this.coordinates = new CoordinatesWithAltitude(longitude, lat, alt);
    }

    public Position(final String name,
                    final List<String> tags,
                    final CoordinatesWithAltitude coords) {
        this.name = name;
        this.tags = tags;
        this.coordinates = coords;
    }

    public CoordinatesWithAltitude getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public static final class PositionBuilder {
        private String name;
        private List<String> tags;
        private CoordinatesWithAltitude coords;

        private PositionBuilder() {
        }

        public static PositionBuilder aPosition() {
            return new PositionBuilder();
        }

        public PositionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PositionBuilder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public PositionBuilder withCoords(CoordinatesWithAltitude coords) {
            this.coords = coords;
            return this;
        }

        public Position build() {
            return new Position(name, tags, coords);
        }
    }
}
