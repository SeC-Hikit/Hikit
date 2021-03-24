package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PointGeolocationDto {
    private CoordinatesDto coordinatesDto;
    private double distance;
}
