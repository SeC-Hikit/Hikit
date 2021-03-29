package org.sc.common.rest.geo;

import lombok.Data;
import org.sc.data.model.Coordinates2D;

import java.util.List;

@Data
public class GeoLineDto {
    private List<Coordinates2D> coordinates;
}
