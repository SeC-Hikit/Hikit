package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper implements Mapper<Place> {

    final TrailCoordinatesMapper coordinatesMapper;

    @Autowired
    public PlaceMapper(TrailCoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public Place mapToObject(Document document) {
        return new Place(
                document.getString(Place.ID),
                document.getString(Place.NAME),
                document.getString(Place.DESCRIPTION),
                document.getList(Place.TAGS, String.class),
                document.getList(Place.MEDIA_IDS, String.class),
                coordinatesMapper.mapToObject(document.get(Place.COORDINATES, Document.class)),
                document.getList(Place.CROSSING, String.class));
    }

    @Override
    public Document mapToDocument(Place object) {
        return new Document(Place.ID, object.getId())
                        .append(Place.NAME, object.getName())
                        .append(Place.DESCRIPTION, object.getDescription())
                        .append(Place.TAGS, object.getTags())
                        .append(Place.MEDIA_IDS, object.getMediaIds())
                        .append(Place.CROSSING, object.getCrossingTrailIds())
                        .append(Place.COORDINATES, coordinatesMapper.mapToDocument(object.getCoordinates()));
    }
}
