package org.sc.common.rest;

import java.util.List;

public class TrailPreparationModelDto {
    private final String name;
    private final String description;
    private final PositionDto startPos;
    private final PositionDto finalPos;
    private final List<TrailCoordinatesDto> coordinates;

    public TrailPreparationModelDto(String name, String description, PositionDto startPos,
                                    PositionDto finalPos, List<TrailCoordinatesDto> coordinates) {
        this.name = name;
        this.description = description;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PositionDto getStartPos() {
        return startPos;
    }

    public PositionDto getFinalPos() {
        return finalPos;
    }

    public List<TrailCoordinatesDto> getCoordinates() {
        return coordinates;
    }
}