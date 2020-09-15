package org.sc.data;

import com.google.inject.Inject;
import org.bson.Document;
import java.util.List;

public class PositionMapper implements Mapper<Position> {

    final CoordinatesAltitudeMapper coordinatesMapper;

    @Inject
    public PositionMapper(CoordinatesAltitudeMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public Position mapToObject(Document document) {
        return Position.PositionBuilder.aPosition()
                .withName(document.getString(Position.NAME))
                .withCoords(coordinatesMapper.mapToObject(document.get(Position.LOCATION, Document.class)))
                .withPostCode(document.getString(Position.POSTCODE))
                .withDescription(document.getString(Position.DESCRIPTION))
                .withTags(document.get(Position.TAGS, List.class))
                .build();
    }

    @Override
    public Document mapToDocument(Position object) {
        return new Document(Position.LOCATION, coordinatesMapper.mapToDocument(object.getCoords()))
                .append(Position.TAGS, object.getTags())
                .append(Position.DESCRIPTION, object.getDescription())
                .append(Position.POSTCODE, object.getPostCode()).append(Position.NAME, object.getName());
    }
}
