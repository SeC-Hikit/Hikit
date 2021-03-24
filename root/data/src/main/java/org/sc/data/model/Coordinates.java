package org.sc.data.model;

public interface Coordinates {

    String COORDINATES = "coordinates";
    String GEO_TYPE = "type";
    String ALTITUDE = "altitude";

    double getLongitude();
    double getLatitude();
    double getAltitude();
}
