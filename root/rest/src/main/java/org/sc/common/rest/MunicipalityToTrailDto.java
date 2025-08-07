package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sc.data.model.Coordinates2D;

import java.util.List;

@Data
@AllArgsConstructor
public class MunicipalityToTrailDto {
    private List<CoordinatesDto> intersectionPoints;
    private Double distance;
    private MunicipalityDetailsDto details;
    private List<Coordinates2D> shapePoints;
}
