package org.sc.data;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class CoordinatesAltitudeMapper implements Mapper<CoordinatesWithAltitude> {

    public static final String POINT_GEO_JSON = "Point";
    public static final int ALTITUDE_INDEX = 2;

    @Override
    public CoordinatesWithAltitude mapToObject(final Document document) {
        final List<Double> list = document.get(CoordinatesWithAltitude.COORDINATES, List.class);
        return CoordinatesWithAltitude.CoordinatesWithAltitudeBuilder.aCoordinatesWithAltitude()
                .withLongitude(list.get(Coordinates.LONG_INDEX)).withLatitude(list.get(Coordinates.LAT_INDEX)).withAltitude(list.get(ALTITUDE_INDEX)).build();
    }

    @Override
    public Document mapToDocument(final CoordinatesWithAltitude object) {
        return new Document(CoordinatesWithAltitude.GEO_TYPE, POINT_GEO_JSON)
                .append(CoordinatesWithAltitude.COORDINATES,
                        Arrays.asList(object.getLongitude(), object.getLatitude(), object.getAltitude()));
    }
}
