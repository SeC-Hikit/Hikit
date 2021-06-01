package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class GeoLineMapper implements Mapper<GeoLineString> {

    @Override
    public GeoLineString mapToObject(Document document) {
        return new GeoLineString(getListOfPairCoordinates(document));
    }

    @Override
    public Document mapToDocument(GeoLineString object) {
        return new Document(GeoLineString.TYPE, GeoLineString.GEO_TYPE)
                .append(GeoLineString.COORDINATES, getListOfCoordinates(object.getCoordinates()));
    }

    public Document mapCoordsToDocument(List<TrailCoordinates> trailCoordinates) {
        List<List<Double>> ownCoords = getListOfCoordinates(
                trailCoordinates
                        .stream()
                        .map(a -> new Coordinates2D(a.getLongitude(),
                                a.getLatitude())).collect(toList()));
        return new Document(GeoLineString.TYPE, GeoLineString.GEO_TYPE)
                .append(GeoLineString.COORDINATES, ownCoords);
    }

    public List<List<Double>> getListOfCoordinates(final List<Coordinates2D> coordinate2DS) {
        return coordinate2DS.stream().map(Coordinates2D::getAsList).collect(Collectors.toList());
    }

    private List<Coordinates2D> getListOfPairCoordinates(Document document) {
        final List<List> generalList = document.getList(Trail.COORDINATES, List.class);
        return generalList.stream().map(entry -> new Coordinates2D((Double) (entry.get(CoordinatesWithAltitude.LONG_INDEX)),
                (Double) entry.get(CoordinatesWithAltitude.LAT_INDEX))).collect(Collectors.toList());
    }
}
