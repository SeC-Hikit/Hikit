package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrailIntersection {
    private Trail trail;
    private List<Coordinates> points;
}
