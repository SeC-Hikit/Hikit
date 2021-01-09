package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.PoiMacroType;
import org.sc.common.rest.TrailCoordinates;
import org.sc.data.entity.Poi;

import java.util.List;

public class PoiMapper implements Mapper<Poi> {

    final TrailCoordinatesMapper coordinatesMapper;

    public PoiMapper(final TrailCoordinatesMapper coordinatesMapper) {
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
                document.getDate(Poi.LAST_UPDATE_ON));
    }

    @Override
    public Document mapToDocument(final Poi object) {
        return new Document(Poi.NAME, object.getName())
                .append(Poi.DESCRIPTION, object.getDescription())
                .append(Poi.TAGS, object.getTags())
                .append(Poi.MACROTYPE, object.getMacroType().toString())
                .append(Poi.MICROTYPES, object.getMicroType())
                .append(Poi.MEDIA_IDS, object.getMediaIds())
                .append(Poi.TRAIL_CODES, object.getTrailCodes())
                .append(Poi.TRAIL_COORDINATES, coordinatesMapper.mapToDocument(object.getTrailCoordinates()))
                .append(Poi.CREATED_ON, object.getCreatedOn())
                .append(Poi.LAST_UPDATE_ON, object.getLastUpdatedOn());
    }

    private TrailCoordinates getCoordinatesWithAltitude(final Document doc) {
        final Document document = doc.get(Poi.TRAIL_COORDINATES, Document.class);
        return coordinatesMapper.mapToObject(document);
    }

}
