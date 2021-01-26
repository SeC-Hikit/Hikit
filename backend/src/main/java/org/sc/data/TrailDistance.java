package org.sc.data;

import org.sc.common.rest.Coordinates;
import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.TrailDto;
import org.sc.data.entity.CoordinatesWithAltitude;
import org.sc.data.entity.Trail;

public class TrailDistance {

    private final CoordinatesDto coordinates;
    private int distance;
    private TrailDto trail;

    public TrailDistance(int distance, CoordinatesDto coordinates, TrailDto trail) {
        this.distance = distance;
        this.coordinates = coordinates;
        this.trail = trail;
    }

    public int getDistance() {
        return distance;
    }

    public TrailDto getTrail() {
        return trail;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
