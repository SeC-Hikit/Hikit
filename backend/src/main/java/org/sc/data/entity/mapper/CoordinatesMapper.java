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
public class CoordinatesMapper implements Mapper<Coordinates> {
    private static final Logger LOGGER = getLogger(CoordinatesMapper.class);

    public static final String POINT_GEO_JSON = "Point";

    @Override
    public CoordinatesWithAltitude mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        final List<Double> list = document.getList(Coordinates.COORDINATES, Double.class);
        final Double altitude = document.getDouble(Coordinates.ALTITUDE);
        return new CoordinatesWithAltitude(
                list.get(CoordinatesWithAltitude.LAT_INDEX),
                list.get(CoordinatesWithAltitude.LONG_INDEX),
                altitude);
    }

    @Override
    public Document mapToDocument(final Coordinates object) {
        LOGGER.trace("mapToDocument Coordinates: {} ", object);
        return new Document(TrailCoordinates.GEO_TYPE, POINT_GEO_JSON)
                .append(TrailCoordinates.COORDINATES,
                        Arrays.asList(object.getLongitude(), object.getLatitude()))
                .append(TrailCoordinates.ALTITUDE, object.getAltitude());
    }
}
