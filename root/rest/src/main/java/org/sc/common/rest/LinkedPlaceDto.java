package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkedPlaceDto {
    private String trailId;
    private String placeId;
    private CoordinatesDto coordinatesDto;
}
