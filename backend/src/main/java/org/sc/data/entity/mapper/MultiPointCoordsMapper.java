package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.MultiPointCoords2D;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class MultiPointCoordsMapper implements Mapper<MultiPointCoords2D> {
    private static final Logger LOGGER = getLogger(MultiPointCoordsMapper.class);
    public static final String MULTIPOINT_GEO_JSON = "MultiPoint";

    @Override
    public MultiPointCoords2D mapToObject(Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        final List<List> listOfCoords = document.getList(MultiPointCoords2D.COORDINATES, List.class);
        final List<List<Double>> constructedPairs = new ArrayList<>();
        listOfCoords.forEach(pair-> constructedPairs.add(Arrays.asList(
                (Double) pair.get(CoordinatesWithAltitude.LONG_INDEX),
                (Double) pair.get(CoordinatesWithAltitude.LAT_INDEX))));
        return new MultiPointCoords2D(constructedPairs);
    }

    @Override
    public Document mapToDocument(MultiPointCoords2D object) {
        LOGGER.trace("mapToDocument MultiPointCoords2D: {} ", object);
        return new Document(MultiPointCoords2D.TYPE, MULTIPOINT_GEO_JSON)
                .append(MultiPointCoords2D.COORDINATES, object.getCoordinates2D());
    }
}
