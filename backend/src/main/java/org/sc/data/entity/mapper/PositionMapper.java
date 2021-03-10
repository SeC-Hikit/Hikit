package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.Position;
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
        return new Position(document.getString(Position.NAME),
                document.getList(Position.TAGS, String.class),
                coordinatesMapper.mapToObject(document.get(Position.COORDINATES, Document.class)),
                document.getList(Position.MEDIA_IDS, String.class));
    }

    @Override
    public Document mapToDocument(Position object) {
        return new Document(Position.COORDINATES, coordinatesMapper.mapToDocument(object.getCoordinates()))
                .append(Position.TAGS, object.getTags()).append(Position.NAME, object.getName());
    }
}
