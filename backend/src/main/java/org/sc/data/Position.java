package org.sc.data;


import java.util.List;

public class Position {

    public static final String POSTCODE = "postCode";
    public static final String LOCATION = "location";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TAGS = "tags";

    private String name;
    private String description;
    private List<String> tags;
    private CoordinatesWithAltitude coords;
    private String postCode;

    public Position(double alt, double lat, double longitude) {
        this.coords = new CoordinatesWithAltitude(longitude, lat, alt);
    }

    public Position(final String name,
                    final String description,
                    final List<String> tags,
                    final CoordinatesWithAltitude coords,
                    final String postCode) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.coords = coords;
        this.postCode = postCode;
    }

    public CoordinatesWithAltitude getCoords() {
        return coords;
    }

    public String getPostCode() {
        return postCode;
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

    public static final class PositionBuilder {
        private String name;
        private String description;
        private List<String> tags;
        private CoordinatesWithAltitude coords;
        private String postCode;

        private PositionBuilder() {
        }

        public static PositionBuilder aPosition() {
            return new PositionBuilder();
        }

        public PositionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PositionBuilder withDescription(String description) {
            this.description = description;
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

        public PositionBuilder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Position build() {
            return new Position(name, description, tags, coords, postCode);
        }
    }
}
