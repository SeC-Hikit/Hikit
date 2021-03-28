package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrailIntersectionDto {
    private CoordinatesDto coordinatesDto;
    private TrailDto trailDto;
}
