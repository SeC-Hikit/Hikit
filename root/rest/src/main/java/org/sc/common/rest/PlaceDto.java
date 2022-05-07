package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PlaceDto {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private List<String> mediaIds;
    private List<CoordinatesDto> coordinates;
    private List<String> crossingTrailIds;
    private boolean isDynamic;
    private RecordDetailsDto recordDetails;
}
