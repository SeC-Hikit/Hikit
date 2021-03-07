package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class TrailMapper implements Mapper<Trail> {

    protected final PositionMapper positionMapper;
    protected final TrailCoordinatesMapper trailCoordinatesMapper;
    protected final GeoLineMapper geoLineMapper;
    protected final StatsTrailMapper statsTrailMapper;
    private final LinkedMediaMapper linkedMediaMapper;

    @Autowired
    public TrailMapper(final PositionMapper positionMapper,
                       final TrailCoordinatesMapper trailCoordinatesMapper,
                       final GeoLineMapper geoLineMapper,
                       final StatsTrailMapper statsTrailMapper,
                       final LinkedMediaMapper linkedMediaMapper) {
        this.positionMapper = positionMapper;
        this.trailCoordinatesMapper = trailCoordinatesMapper;
        this.geoLineMapper = geoLineMapper;
        this.statsTrailMapper = statsTrailMapper;
        this.linkedMediaMapper = linkedMediaMapper;
    }

    @Override
    public Trail mapToObject(final Document doc) {
        return Trail.builder()
                .id(doc.getString(Trail.ID))
                .name(doc.getString(Trail.NAME))
                .description(doc.getString(Trail.DESCRIPTION))
                .code(doc.getString(Trail.CODE))
                .officialEta(doc.getInteger(Trail.OFFICIAL_ETA))
                .variant(doc.getBoolean(Trail.VARIANT))
                .startPos(getPos(doc, Trail.START_POS))
                .finalPos(getPos(doc, Trail.FINAL_POS))
                .locations(getLocations(doc))
                .classification(getClassification(doc))
                .statsTrailMetadata(getMetadata(doc.get(Trail.STATS_METADATA, Document.class)))
                .country(doc.getString(Trail.COUNTRY))
                .coordinates(getCoordinatesWithAltitude(doc))
                .createdOn(getCreatedDate(doc))
                .lastUpdate(getLastUpdateDate(doc))
                .maintainingSection(doc.getString(Trail.SECTION_CARED_BY))
                .territorialDivision(doc.getString(Trail.TERRITORIAL_CARED_BY))
                .geoLine(getGeoLine(doc.get(Trail.GEO_LINE, Document.class)))
                .mediaList(getLinkedMediaMapper(doc))
                .build();
    }

    @Override
    public Document mapToDocument(final Trail object) {
        return new Document()
                .append(Trail.NAME, object.getName())
                .append(Trail.DESCRIPTION, object.getDescription())
                .append(Trail.CODE, object.getCode())
                .append(Trail.OFFICIAL_ETA, object.getOfficialEta())
                .append(Trail.START_POS, positionMapper.mapToDocument(object.getStartPos()))
                .append(Trail.FINAL_POS, positionMapper.mapToDocument(object.getFinalPos()))
                .append(Trail.LOCATIONS, object.getLocations().stream()
                        .map(positionMapper::mapToDocument).collect(toList()))
                .append(Trail.CLASSIFICATION, object.getClassification().toString())
                .append(Trail.COUNTRY, object.getCountry())
                .append(Trail.SECTION_CARED_BY, object.getMaintainingSection())
                .append(Trail.LAST_UPDATE_DATE, new Date())
                .append(Trail.VARIANT, object.isVariant())
                .append(Trail.TERRITORIAL_CARED_BY, object.getTerritorialDivision())
                .append(Trail.STATS_METADATA, statsTrailMapper.mapToDocument(object.getStatsTrailMetadata()))
                .append(Trail.COORDINATES, object.getCoordinates().stream()
                        .map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(Trail.MEDIA, object.getMediaList().stream()
                        .map(linkedMediaMapper::mapToDocument)
                        .collect(toList()))
                .append(Trail.GEO_LINE, geoLineMapper.mapToDocument(object.getGeoLineString()));
    }

    protected GeoLineString getGeoLine(final Document doc) {
        return geoLineMapper.mapToObject(doc);
    }

    protected StatsTrailMetadata getMetadata(final Document doc) {
        return new StatsTrailMetadata(doc.getDouble(StatsTrailMetadata.TOTAL_RISE),
                doc.getDouble(StatsTrailMetadata.TOTAL_FALL),
                doc.getDouble(StatsTrailMetadata.ETA),
                doc.getDouble(StatsTrailMetadata.LENGTH));
    }

    protected List<LinkedMedia> getLinkedMediaMapper(Document doc) {
        List<Document> list = doc.getList(Poi.MEDIA, Document.class);
        return list.stream().map(linkedMediaMapper::mapToObject).collect(toList());
    }

    private List<TrailCoordinates> getCoordinatesWithAltitude(final Document doc) {
        final List<Document> list = doc.getList(Trail.COORDINATES, Document.class);
        return list.stream().map(trailCoordinatesMapper::mapToObject).collect(toList());
    }

    protected List<Position> getLocations(final Document doc) {
        List<Document> list = doc.getList(Trail.LOCATIONS, Document.class);
        return list.stream().map(positionMapper::mapToObject).collect(toList());
    }

    protected Position getPos(final Document doc,
                            final String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return positionMapper.mapToObject(pos);
    }

    protected Date getLastUpdateDate(Document doc) {
        return doc.getDate(Trail.LAST_UPDATE_DATE);
    }

    protected TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }

}
