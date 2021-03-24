package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceRefDto {
    private String name;
    private TrailCoordinatesDto trailCoordinates;
    private String placeId;
}
