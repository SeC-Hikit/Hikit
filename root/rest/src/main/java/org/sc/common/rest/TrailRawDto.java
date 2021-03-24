package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class TrailRawDto {
    private String id;
    private String name;
    private String description;
    private TrailCoordinatesDto startPos;
    private TrailCoordinatesDto finalPos;
    private List<TrailCoordinatesDto> coordinates;
    private FileDetailsDto fileDetails;
}