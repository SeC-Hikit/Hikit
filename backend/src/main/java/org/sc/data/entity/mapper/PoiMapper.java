package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.PoiMacroType;
import org.sc.data.entity.CoordinatesWithAltitude;
import org.sc.data.entity.Poi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PoiMapper implements Mapper<Poi> {

    final CoordinatesMapper coordinatesMapper;

    public PoiMapper(final CoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public Poi mapToObject(final Document document) {
        return new Poi(document.getString(Poi.OBJECT_ID),
                document.getString(Poi.NAME),
                document.getString(Poi.DESCRIPTION),
                document.get(Poi.TAGS, List.class),
                PoiMacroType.valueOf(document.getString(Poi.MACROTYPE)),
                document.get(Poi.MICROTYPES, List.class),
                document.get(Poi.MEDIA_IDS, List.class),
                document.get(Poi.TRAIL_CODES, List.class),
                getCoordinatesWithAltitude(document),
                document.getDate(Poi.CREATED_ON),
                document.getDate(Poi.LAST_UPDATE_ON),
                document.get(Poi.EXTERNAL_RESOURCES, List.class));
    }

    @Override
    public Document mapToDocument(final Poi poi) {
        return new Document(Poi.NAME, poi.getName())
                .append(Poi.DESCRIPTION, poi.getDescription())
                .append(Poi.TAGS, poi.getTags())
                .append(Poi.MACROTYPE, poi.getMacroType().toString())
                .append(Poi.MICROTYPES, poi.getMicroType())
                .append(Poi.MEDIA_IDS, poi.getMediaIds())
                .append(Poi.TRAIL_CODES, poi.getTrailIds())
                .append(Poi.TRAIL_COORDINATES, coordinatesMapper.mapToDocument(poi.getCoordinates()))
                .append(Poi.CREATED_ON, poi.getCreatedOn())
                .append(Poi.LAST_UPDATE_ON, poi.getLastUpdatedOn())
                .append(Poi.EXTERNAL_RESOURCES, poi.getExternalResources());
    }

    private CoordinatesWithAltitude getCoordinatesWithAltitude(final Document doc) {
        final Document document = doc.get(Poi.TRAIL_COORDINATES, Document.class);
        return coordinatesMapper.mapToObject(document);
    }

}
