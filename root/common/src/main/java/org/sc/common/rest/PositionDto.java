package org.sc.common.rest;

import java.util.List;

public class PositionDto {
    private String name;
    private List<String> tags;
    private TrailCoordinatesDto coordinates;

    public PositionDto() {
    }

    public PositionDto(String name, List<String> tags,
                       TrailCoordinatesDto coordinates) {
        this.name = name;
        this.tags = tags;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public TrailCoordinatesDto getCoordinates() {
        return coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCoordinates(TrailCoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }
}