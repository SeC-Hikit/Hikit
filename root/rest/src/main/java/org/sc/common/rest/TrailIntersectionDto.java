package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrailIntersectionDto {
    private List<CoordinatesDto> points;
    private TrailDto trail;
}
