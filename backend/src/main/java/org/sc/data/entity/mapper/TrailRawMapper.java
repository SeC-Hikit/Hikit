package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class TrailRawMapper implements Mapper<TrailRaw> {
    private static final Logger LOGGER = getLogger(TrailRawMapper.class);

    protected final TrailCoordinatesMapper trailCoordinatesMapper;
    protected final FileDetailsMapper fileDetailsMapper;

    @Autowired
    public TrailRawMapper(final TrailCoordinatesMapper trailCoordinatesMapper,
                          FileDetailsMapper fileDetailsMapper) {
        this.trailCoordinatesMapper = trailCoordinatesMapper;
        this.fileDetailsMapper = fileDetailsMapper;
    }

    @Override
    public TrailRaw mapToObject(final Document doc) {
        LOGGER.trace("mapToObject Document: {} ", doc);
        return TrailRaw.builder()
                .id(doc.getString(TrailRaw.ID))
                .name(doc.getString(TrailRaw.NAME))
                .description(doc.getString(TrailRaw.DESCRIPTION))
                .startPos(trailCoordinatesMapper.mapToObject(doc.get(TrailRaw.START_POS, Document.class)))
                .finalPos(trailCoordinatesMapper.mapToObject(doc.get(TrailRaw.FINAL_POS, Document.class)))
                .coordinates(doc.getList(TrailRaw.COORDINATES, Document.class)
                        .stream().map(trailCoordinatesMapper::mapToObject).collect(toList()))
                .fileDetails(fileDetailsMapper.mapToObject(doc.get(TrailRaw.FILE_DETAILS, Document.class)))
                .build();
    }

    @Override
    public Document mapToDocument(final TrailRaw object) {
        LOGGER.trace("mapToDocument TrailRaw: {} ", object);
        return new Document()
                .append(TrailRaw.ID, object.getId())
                .append(TrailRaw.NAME, object.getName())
                .append(TrailRaw.DESCRIPTION, object.getDescription())
                .append(TrailRaw.COORDINATES, object.getCoordinates().stream().map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(TrailRaw.START_POS, trailCoordinatesMapper.mapToDocument(object.getStartPos()))
                .append(TrailRaw.FINAL_POS, trailCoordinatesMapper.mapToDocument(object.getFinalPos()))
                .append(TrailRaw.FILE_DETAILS, fileDetailsMapper.mapToDocument(object.getFileDetails()));
    }
}
