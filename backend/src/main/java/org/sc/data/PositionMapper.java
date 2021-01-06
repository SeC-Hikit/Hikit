package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PositionMapper implements Mapper<Position> {

    final TrailCoordinatesMapper coordinatesMapper;

    @Autowired
    public PositionMapper(TrailCoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public Position mapToObject(Document document) {
        return Position.PositionBuilder.aPosition()
                .withName(document.getString(Position.NAME))
                .withCoords(coordinatesMapper.mapToObject(document.get(Position.COORDINATES, Document.class)))
                .withTags(document.get(Position.TAGS, List.class))
                .build();
    }

    @Override
    public Document mapToDocument(Position object) {
        return new Document(Position.COORDINATES, coordinatesMapper.mapToDocument(object.getCoordinates()))
                .append(Position.TAGS, object.getTags()).append(Position.NAME, object.getName());
    }
}
