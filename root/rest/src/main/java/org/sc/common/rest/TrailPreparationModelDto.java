package org.sc.common.rest;

import java.util.List;
import java.util.Objects;

public class TrailPreparationModelDto {
    private String name;
    private String description;
    private PlaceDto startPos;
    private PlaceDto finalPos;
    private List<TrailCoordinatesDto> coordinates;

    public TrailPreparationModelDto() {
    }

    public TrailPreparationModelDto(String name, String description, PlaceDto startPos,
                                    PlaceDto finalPos, List<TrailCoordinatesDto> coordinates) {
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

    public PlaceDto getStartPos() {
        return startPos;
    }

    public PlaceDto getFinalPos() {
        return finalPos;
    }

    public List<TrailCoordinatesDto> getCoordinates() {
        return coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartPos(PlaceDto startPos) {
        this.startPos = startPos;
    }

    public void setFinalPos(PlaceDto finalPos) {
        this.finalPos = finalPos;
    }

    public void setCoordinates(List<TrailCoordinatesDto> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrailPreparationModelDto that = (TrailPreparationModelDto) o;
        return getName().equals(that.getName()) &&
                getDescription().equals(that.getDescription()) &&
                getStartPos().equals(that.getStartPos()) &&
                getFinalPos().equals(that.getFinalPos()) &&
                getCoordinates().equals(that.getCoordinates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(),
                getStartPos(), getFinalPos(), getCoordinates());
    }
}