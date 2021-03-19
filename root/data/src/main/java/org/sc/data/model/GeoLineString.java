package org.sc.data.model;

import java.util.List;

public class GeoLineString {
    
    public static String GEO_TYPE = "LineString";
    public static String TYPE = "type";
    public static String COORDINATES = "coordinates";

    private final List<Coordinates2D> coordinates;

    public GeoLineString(List<Coordinates2D> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Coordinates2D> getCoordinates() {
        return coordinates;
    }
}
