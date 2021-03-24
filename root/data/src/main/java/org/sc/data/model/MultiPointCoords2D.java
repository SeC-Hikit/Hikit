package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MultiPointCoords2D {

    public static final String TYPE = "type";
    public static final String COORDINATES = "coordinates";

    private List<List<Double>> coordinates2D;
}
