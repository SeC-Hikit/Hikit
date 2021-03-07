package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.KeyVal;
import org.sc.data.model.Poi;
import org.sc.data.model.PoiMacroType;
import org.sc.data.model.LinkedMedia;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class PoiMapper implements Mapper<Poi> {

    private final CoordinatesMapper coordinatesMapper;
    private final KeyValMapper keyValMapper;
    private final LinkedMediaMapper linkedMediaMapper;

    public PoiMapper(final CoordinatesMapper coordinatesMapper,
                     final KeyValMapper keyValMapper,
                     final LinkedMediaMapper linkedMediaMapper) {
        this.coordinatesMapper = coordinatesMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.keyValMapper = keyValMapper;
    }

    @Override
    public Poi mapToObject(final Document document) {
        return new Poi(document.getString(Poi.OBJECT_ID),
                document.getString(Poi.NAME),
                document.getString(Poi.DESCRIPTION),
                document.getList(Poi.TAGS, String.class),
                PoiMacroType.valueOf(document.getString(Poi.MACROTYPE)),
                document.getList(Poi.MICROTYPES, String.class),
                getLinkedMediaMapper(document),
                document.getList(Poi.TRAIL_CODES, String.class),
                getCoordinatesWithAltitude(document),
                document.getDate(Poi.CREATED_ON),
                document.getDate(Poi.LAST_UPDATE_ON),
                document.getList(Poi.EXTERNAL_RESOURCES, String.class),
                getKeyVals(document));
    }

    @Override
    public Document mapToDocument(final Poi poi) {
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
                .append(Poi.CREATED_ON, poi.getCreatedOn())
                .append(Poi.LAST_UPDATE_ON, poi.getLastUpdatedOn())
                .append(Poi.EXTERNAL_RESOURCES, poi.getExternalResources())
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
