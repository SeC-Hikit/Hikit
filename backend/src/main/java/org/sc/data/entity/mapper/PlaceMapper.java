package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class PlaceMapper implements Mapper<Place> {
    private static final Logger LOGGER = getLogger(PlaceMapper.class);

    final MultiPointCoordsMapper multiPointCoordsMapperMapper;
    final CoordinatesMapper coordinatesMapper;
    final RecordDetailsMapper recordDetailsMapper;

    @Autowired
    public PlaceMapper(final MultiPointCoordsMapper multiPointCoordsMapper,
                       final CoordinatesMapper coordinatesMapper,
                       final RecordDetailsMapper recordDetailsMapper) {
        this.multiPointCoordsMapperMapper = multiPointCoordsMapper;
        this.coordinatesMapper = coordinatesMapper;
        this.recordDetailsMapper = recordDetailsMapper;
    }

    @Override
    public Place mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new Place(
                document.getString(Place.ID),
                document.getString(Place.NAME),
                document.getString(Place.DESCRIPTION),
                document.getList(Place.TAGS, String.class),
                document.getList(Place.MEDIA_IDS, String.class),
                multiPointCoordsMapperMapper.mapToObject(document.get(Place.POINTS, Document.class)),
                document.getList(Place.COORDINATES, Document.class).stream()
                        .map(coordinatesMapper::mapToObject).collect(Collectors.toList()),
                document.getList(Place.CROSSING, String.class),
                recordDetailsMapper.mapToObject(document.get(Place.RECORD_DETAILS, Document.class)));
    }

    @Override
    public Document mapToDocument(final Place object) {
        LOGGER.trace("mapToDocument Place: {} ", object);
        return new Document(Place.ID, object.getId())
                .append(Place.NAME, object.getName())
                .append(Place.DESCRIPTION, object.getDescription())
                .append(Place.TAGS, object.getTags())
                .append(Place.MEDIA_IDS, object.getMediaIds())
                .append(Place.CROSSING, object.getCrossingTrailIds())
                .append(Place.POINTS, multiPointCoordsMapperMapper.mapToDocument(object.getPoints()))
                .append(Place.RECORD_DETAILS, recordDetailsMapper.mapToDocument(object.getRecordDetails()))
                .append(Place.COORDINATES, object.getCoordinates()
                        .stream().map(coordinatesMapper::mapToDocument)
                        .collect(Collectors.toList()));
    }
}
