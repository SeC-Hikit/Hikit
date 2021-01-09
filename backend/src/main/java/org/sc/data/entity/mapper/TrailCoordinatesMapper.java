package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.CoordinatesWithAltitude;
import org.sc.common.rest.TrailCoordinates;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TrailCoordinatesMapper implements Mapper<TrailCoordinates> {

    public static final String POINT_GEO_JSON = "Point";

    @Override
    public TrailCoordinates mapToObject(final Document document) {
        final List<Double> list = document.get(CoordinatesWithAltitude.COORDINATES, List.class);
        final Double altitude = document.getDouble(TrailCoordinates.ALTITUDE);
        final Integer distanceProgress = document.getInteger(TrailCoordinates.DISTANCE_FROM_START);
        return TrailCoordinates.TrailCoordinatesBuilder.aTrailCoordinates()
                .withLongitude(list.get(TrailCoordinates.LONG_INDEX))
                .withLatitude(list.get(TrailCoordinates.LAT_INDEX))
                .withDistanceFromTrailStart(distanceProgress)
                .withAltitude(altitude).build();
    }

    @Override
    public Document mapToDocument(final TrailCoordinates object) {
        return new Document(TrailCoordinates.GEO_TYPE, POINT_GEO_JSON)
                .append(TrailCoordinates.COORDINATES,
                        Arrays.asList(object.getLongitude(), object.getLatitude()))
                .append(TrailCoordinates.ALTITUDE, object.getAltitude())
                .append(TrailCoordinates.DISTANCE_FROM_START, object.getDistanceFromTrailStart());
    }
}
