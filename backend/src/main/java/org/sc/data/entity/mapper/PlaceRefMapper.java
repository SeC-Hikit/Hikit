package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.PlaceRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class PlaceRefMapper implements Mapper<PlaceRef> {
    private static final Logger LOGGER = getLogger(PlaceRefMapper.class);

    final CoordinatesMapper coordinatesMapper;

    @Autowired
    public PlaceRefMapper(final CoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public PlaceRef mapToObject(Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new PlaceRef(document.getString(PlaceRef.NAME),
                coordinatesMapper.mapToObject(document.get(PlaceRef.COORDINATES, Document.class)),
                document.getString(PlaceRef.PLACE_ID),
                document.getList(PlaceRef.ENCOUNTERED_TRAIL_IDS, String.class),
                document.getBoolean(PlaceRef.IS_DYNAMIC));
    }

    @Override
    public Document mapToDocument(PlaceRef object) {
        LOGGER.trace("mapToDocument PlaceRef: {} ", object);
        return new Document(PlaceRef.NAME, object.getName())
                .append(PlaceRef.PLACE_ID, object.getPlaceId())
                .append(PlaceRef.ENCOUNTERED_TRAIL_IDS, object.getEncounteredTrailIds())
                .append(PlaceRef.COORDINATES, coordinatesMapper.mapToDocument(object.getCoordinates()))
                .append(PlaceRef.IS_DYNAMIC, coordinatesMapper.mapToDocument(object.getCoordinates()));
    }
}
