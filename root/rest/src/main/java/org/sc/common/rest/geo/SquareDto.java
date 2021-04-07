package org.sc.common.rest.geo;

import lombok.Data;
import org.sc.data.model.Coordinates2D;

import java.util.List;

@Data
public class SquareDto {
    private Coordinates2D bottomLeft;
    private Coordinates2D topLeft;
    private Coordinates2D topRight;
    private Coordinates2D bottomRight;
}
