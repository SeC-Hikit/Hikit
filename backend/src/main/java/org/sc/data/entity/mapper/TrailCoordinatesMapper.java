package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.Coordinates;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.TrailCoordinates;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class TrailCoordinatesMapper implements Mapper<TrailCoordinates> {
    private static final Logger LOGGER = getLogger(TrailCoordinatesMapper.class);

    public static final String POINT_GEO_JSON = "Point";

    @Override
    public TrailCoordinates mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        final List<Double> list = document.getList(Coordinates.COORDINATES, Double.class);
        final Double altitude = document.getDouble(TrailCoordinates.ALTITUDE);
        final Integer distanceProgress = document.getInteger(TrailCoordinates.DISTANCE_FROM_START);
        return new TrailCoordinates(list.get(CoordinatesWithAltitude.LAT_INDEX),
                list.get(CoordinatesWithAltitude.LONG_INDEX), altitude, distanceProgress);
    }

    @Override
    public Document mapToDocument(final TrailCoordinates object) {
        LOGGER.trace("mapToDocument TrailCoordinates: {} ", object);
        return new Document(TrailCoordinates.GEO_TYPE, POINT_GEO_JSON)
                .append(TrailCoordinates.COORDINATES,
                        Arrays.asList(object.getLongitude(), object.getLatitude()))
                .append(TrailCoordinates.ALTITUDE, object.getAltitude())
                .append(TrailCoordinates.DISTANCE_FROM_START, object.getDistanceFromTrailStart());
    }
}
