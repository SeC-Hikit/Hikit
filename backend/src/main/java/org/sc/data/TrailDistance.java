package org.sc.data;

import org.sc.common.rest.Coordinates;
import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.TrailDto;

public class TrailDistance {

    private final Coordinates coordinates;
    private final int distance;
    private final TrailDto trail;

    public TrailDistance(final int distance,
                         final Coordinates coordinates,
                         final TrailDto trail) {
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
