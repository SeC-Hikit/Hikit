package org.sc.data.entity;


import java.util.List;
import java.util.stream.Collectors;

public class Position {

    public static final String NAME = "name";
    public static final String TAGS = "tags";
    public static final String COORDINATES = "coordinates";
    public static final String MEDIA_IDS = "mediaIds";

    private String name;
    private List<String> tags;
    private TrailCoordinates coordinates;
    private List<String> mediaIds;

    public Position() {
    }

    public Position(double alt, double lat, double longitude, int distanceFromStart) {
        this.coordinates = new TrailCoordinates(longitude, lat, alt, distanceFromStart);
    }

    public Position(final String name,
                    final List<String> tags,
                    final TrailCoordinates coords) {
        this.name = name;
        this.tags = tags;
        this.coordinates = coords;
    }

    public Position(final String name,
                    final List<String> tags,
                    final TrailCoordinates coords,
                    final List<String> mediaIds) {
        this.name = name;
        this.tags = tags;
        this.coordinates = coords;
        this.mediaIds = mediaIds;
    }

    public TrailCoordinates getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags.stream().filter(a -> !a.equals("")).collect(Collectors.toList());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCoordinates(TrailCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<String> getMediaIds() {
        return mediaIds;
    }
}
