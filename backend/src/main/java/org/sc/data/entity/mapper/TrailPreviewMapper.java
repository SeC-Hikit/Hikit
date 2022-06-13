package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class TrailPreviewMapper implements Mapper<TrailPreview> {
    private static final Logger LOGGER = getLogger(TrailPreviewMapper.class);

    private final PlaceRefMapper placeMapper;
    private final FileDetailsMapper fileDetailsMapper;
    private final CycloMapper cycloMapper;

    @Autowired
    public TrailPreviewMapper(final PlaceRefMapper placeMapper,
                              final FileDetailsMapper fileDetailsMapper,
                              final CycloMapper cycloMapper) {
        this.placeMapper = placeMapper;
        this.fileDetailsMapper = fileDetailsMapper;
        this.cycloMapper = cycloMapper;
    }

    @Override
    public TrailPreview mapToObject(Document doc) {
        LOGGER.trace("mapToObject Document: {} ", doc);
        return new TrailPreview(
                doc.getString(Trail.ID),
                doc.getString(Trail.CODE),
                getClassification(doc),
                getLocations(doc),
                getPos(doc, Trail.START_POS),
                getPos(doc, Trail.FINAL_POS),
                fileDetailsMapper.mapToObject(doc.get(Trail.RECORD_DETAILS, Document.class)),
                !cycloMapper.mapToObject(doc.get(Trail.CYCLO, Document.class)).getCycloClassification()
                        .equals(CycloClassification.UNCLASSIFIED),
                getStatus(doc));
    }

    @Override
    public Document mapToDocument(final TrailPreview object) {
        throw new IllegalStateException();
    }

    private PlaceRef getPos(final Document doc,
                            final String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return placeMapper.mapToObject(pos);
    }

    private List<PlaceRef> getLocations(final Document doc) {
        final List<Document> pos = doc.getList(Trail.LOCATIONS, Document.class);
        return pos.stream().map(placeMapper::mapToObject).collect(Collectors.toList());
    }

    private TrailStatus getStatus(Document doc) {
        final String classification = doc.getString(Trail.STATUS);
        return TrailStatus.valueOf(classification);
    }

    private TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }
}
