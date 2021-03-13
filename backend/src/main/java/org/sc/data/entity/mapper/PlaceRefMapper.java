package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.PlaceRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceRefMapper implements Mapper<PlaceRef> {

    final TrailCoordinatesMapper coordinatesMapper;

    @Autowired
    public PlaceRefMapper(final TrailCoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public PlaceRef mapToObject(Document document) {
        return new PlaceRef(document.getString(PlaceRef.NAME),
                coordinatesMapper.mapToObject(document.get(PlaceRef.COORDINATES, Document.class)),
                document.getString(PlaceRef.PLACE_ID));
    }

    @Override
    public Document mapToDocument(PlaceRef object) {
        return new Document(PlaceRef.NAME, object.getName())
                .append(PlaceRef.PLACE_ID, object.getPlaceId())
                .append(PlaceRef.COORDINATES, coordinatesMapper.mapToDocument(object.getTrailCoordinates()));
    }
}
