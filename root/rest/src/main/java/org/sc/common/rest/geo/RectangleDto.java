package org.sc.common.rest.geo;

import lombok.Data;
import org.sc.data.model.Coordinates2D;

import java.util.List;

@Data
public class RectangleDto {
    private Coordinates2D bottomLeft;
    private Coordinates2D topRight;

}
