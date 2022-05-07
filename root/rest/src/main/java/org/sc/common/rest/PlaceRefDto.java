package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaceRefDto {
    private String name;
    private CoordinatesDto coordinates;
    private String placeId;
    private List<String> encounteredTrailIds;
    private boolean dynamicCrossway;
}
