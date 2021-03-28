package org.sc.data.entity.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sc.data.model.Coordinates;
import org.sc.data.model.Trail;

@Data
@AllArgsConstructor
public class TrailPointIntersection {
    private Coordinates point;
    private Trail trail;
}
