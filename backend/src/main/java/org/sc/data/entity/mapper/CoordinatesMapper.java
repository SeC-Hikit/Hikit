package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.Coordinates;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.TrailCoordinates;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CoordinatesMapper implements Mapper<Coordinates> {

    public static final String POINT_GEO_JSON = "Point";

    @Override
    public CoordinatesWithAltitude mapToObject(final Document document) {
        final List<Double> list = document.getList(CoordinatesWithAltitude.COORDINATES, Double.class);
        final Double altitude = document.getDouble(TrailCoordinates.ALTITUDE);
        return new CoordinatesWithAltitude(list.get(TrailCoordinates.LAT_INDEX), list.get(TrailCoordinates.LONG_INDEX),
                altitude);
    }

    @Override
    public Document mapToDocument(final Coordinates object) {
        return new Document(TrailCoordinates.GEO_TYPE, POINT_GEO_JSON)
                .append(TrailCoordinates.COORDINATES,
                        Arrays.asList(object.getLongitude(), object.getLatitude()))
                .append(TrailCoordinates.ALTITUDE, object.getAltitude());
    }
}
