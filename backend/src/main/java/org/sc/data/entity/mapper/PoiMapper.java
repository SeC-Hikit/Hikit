package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class PoiMapper implements Mapper<Poi> {
    private static final Logger LOGGER = getLogger(PoiMapper.class);

    private final CoordinatesMapper coordinatesMapper;
    private final KeyValMapper keyValMapper;
    private final LinkedMediaMapper linkedMediaMapper;
    private final RecordDetailsMapper recordDetailsMapper;

    public PoiMapper(final CoordinatesMapper coordinatesMapper,
                     final KeyValMapper keyValMapper,
                     final LinkedMediaMapper linkedMediaMapper,
                     final RecordDetailsMapper recordDetailsMapper) {
        this.coordinatesMapper = coordinatesMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.keyValMapper = keyValMapper;
        this.recordDetailsMapper = recordDetailsMapper;
    }

    @Override
    public Poi mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new Poi(document.getString(Poi.OBJECT_ID),
                document.getString(Poi.NAME),
                document.getString(Poi.DESCRIPTION),
                document.getList(Poi.TAGS, String.class),
                PoiMacroType.valueOf(document.getString(Poi.MACROTYPE)),
                document.getList(Poi.MICROTYPES, String.class),
                getLinkedMediaMapper(document),
                document.getList(Poi.TRAIL_CODES, String.class),
                getCoordinatesWithAltitude(document),
                document.getList(Poi.EXTERNAL_RESOURCES, String.class),
                getKeyVals(document),
                document.getString(Poi.EXTERNAL_ID),
                document.getString(Poi.EXTERNAL_SYSTEM_NAME),
                recordDetailsMapper.mapToObject(document.get(Poi.RECORD_DETAILS, Document.class)));
    }

    @Override
    public Document mapToDocument(final Poi poi) {
        LOGGER.trace("mapToDocument Poi: {} ", poi);
        return new Document(Poi.NAME, poi.getName())
                .append(Poi.DESCRIPTION, poi.getDescription())
                .append(Poi.TAGS, poi.getTags())
                .append(Poi.MACROTYPE, poi.getMacroType().toString())
                .append(Poi.MICROTYPES, poi.getMicroType())
                .append(Poi.MEDIA, poi.getMediaList().stream()
                        .map(linkedMediaMapper::mapToDocument)
                        .collect(toList()))
                .append(Poi.TRAIL_CODES, poi.getTrailIds())
                .append(Poi.TRAIL_COORDINATES, coordinatesMapper.mapToDocument(poi.getCoordinates()))
                .append(Poi.EXTERNAL_RESOURCES, poi.getExternalResources())
                .append(Poi.RECORD_DETAILS, poi.getRecordDetails())
                .append(Poi.EXTERNAL_ID, poi.getExternalId())
                .append(Poi.EXTERNAL_SYSTEM_NAME, poi.getExternalSystemName())
                .append(Poi.KEY_VAL, poi.getKeyVal().stream()
                        .map(keyValMapper::mapToDocument)
                        .collect(toList()));
    }

    private CoordinatesWithAltitude getCoordinatesWithAltitude(final Document doc) {
        final Document document = doc.get(Poi.TRAIL_COORDINATES, Document.class);
        return coordinatesMapper.mapToObject(document);
    }

    private List<KeyVal> getKeyVals(Document document) {
        List<Document> list = document.getList(Poi.KEY_VAL, Document.class);
        return list.stream().map(keyValMapper::mapToObject).collect(toList());
    }


    private List<LinkedMedia> getLinkedMediaMapper(Document document) {
        List<Document> list = document.getList(Poi.MEDIA, Document.class);
        return list.stream().map(linkedMediaMapper::mapToObject).collect(toList());
    }

}
