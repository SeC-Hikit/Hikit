package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.GeoLineString;
import org.sc.data.model.SimpleCoordinates;
import org.sc.data.model.Trail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    private List<SimpleCoordinates> getListOfPairCoordinates(Document document) {
        final List<List> generalList = document.getList(Trail.COORDINATES, List.class);
        return generalList.stream().map(entry -> new SimpleCoordinates((Double) (entry.get(CoordinatesWithAltitude.LONG_INDEX)),
                (Double) entry.get(CoordinatesWithAltitude.LAT_INDEX))).collect(Collectors.toList());
    }

    private List<List<Double>> getListOfCoordinates(final List<SimpleCoordinates> simpleCoordinates) {
        return simpleCoordinates.stream().map(SimpleCoordinates::getAsList).collect(Collectors.toList());
    }
}
