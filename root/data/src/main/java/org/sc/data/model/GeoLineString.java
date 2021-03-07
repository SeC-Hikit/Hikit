package org.sc.data.model;

import java.util.List;

public class GeoLineString {
    
    public static String GEO_TYPE = "LineString";
    public static String TYPE = "type";
    public static String COORDINATES = "coordinates";

    private final List<SimpleCoordinates> coordinates;

    public GeoLineString(List<SimpleCoordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public List<SimpleCoordinates> getCoordinates() {
        return coordinates;
    }
}
