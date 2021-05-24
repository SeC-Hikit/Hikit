package org.sc.common.rest.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sc.data.model.Coordinates2D;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoLineDto {
    private List<Coordinates2D> coordinates;
}
