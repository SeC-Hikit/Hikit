package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.*;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.model.Trail.*;

@Component
public class TrailMapper implements Mapper<Trail>, SelectiveArgumentMapper<Trail> {
    private static final Logger LOGGER = getLogger(TrailMapper.class);

    protected final PlaceRefMapper placeMapper;
    protected final TrailCoordinatesMapper trailCoordinatesMapper;
    protected final GeoLineMapper geoLineMapper;
    protected final StatsTrailMapper statsTrailMapper;
    protected final LinkedMediaMapper linkedMediaMapper;
    protected final CycloMapper cycloMapper;
    protected final FileDetailsMapper fileDetailsMapper;
    private final StaticTrailDetailsMapper staticTrailDetailsMapper;


    @Autowired
    public TrailMapper(final PlaceRefMapper placeMapper,
                       final TrailCoordinatesMapper trailCoordinatesMapper,
                       final GeoLineMapper geoLineMapper,
                       final StatsTrailMapper statsTrailMapper,
                       final LinkedMediaMapper linkedMediaMapper,
                       final CycloMapper cycloMapper,
                       final FileDetailsMapper fileDetailsMapper,
                       final StaticTrailDetailsMapper staticTrailDetailsMapper) {
        this.placeMapper = placeMapper;
        this.trailCoordinatesMapper = trailCoordinatesMapper;
        this.geoLineMapper = geoLineMapper;
        this.statsTrailMapper = statsTrailMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.cycloMapper = cycloMapper;
        this.fileDetailsMapper = fileDetailsMapper;
        this.staticTrailDetailsMapper = staticTrailDetailsMapper;
    }

    @Override
    public Trail mapToObject(final Document doc) {
        LOGGER.trace("mapToObject Document: {} ", doc);
        return builder()
                .id(doc.getString(ID))
                .name(doc.getString(NAME))
                .description(doc.getString(DESCRIPTION))
                .code(doc.getString(CODE))
                .startLocation(placeMapper.mapToObject(doc.get(START_POS, Document.class)))
                .endLocation(placeMapper.mapToObject(doc.get(FINAL_POS, Document.class)))
                .officialEta(doc.getInteger(OFFICIAL_ETA))
                .variant(doc.getBoolean(VARIANT))
                .locations(getLocations(doc))
                .classification(getClassification(doc))
                .statsTrailMetadata(getMetadata(doc.get(STATS_METADATA, Document.class)))
                .country(doc.getString(COUNTRY))
                .coordinates(getCoordinatesWithAltitude(doc, TrailSimplifierLevel.FULL))
                .lastUpdate(getLastUpdateDate(doc))
                .maintainingSection(doc.getString(SECTION_CARED_BY))
                .territorialDivision(doc.getString(TERRITORIAL_CARED_BY))
                .geoLineString(getGeoLine(doc.get(GEO_LINE, Document.class)))
                .mediaList(getLinkedMediaMapper(doc))
                .cycloDetails(cycloMapper.mapToObject(doc.get(CYCLO, Document.class)))
                .fileDetails(fileDetailsMapper.mapToObject(doc.get(RECORD_DETAILS, Document.class)))
                .staticTrailDetails(staticTrailDetailsMapper.mapToObject(doc.get(STATIC_TRAIL_DETAILS, Document.class)))
                .status(getStatus(doc))
                .build();
    }

    @Override
    public Document mapToDocument(final Trail object) {
        LOGGER.trace("mapToDocument Trail: {} ", object);
        return new Document()
                .append(NAME, object.getName())
                .append(DESCRIPTION, object.getDescription())
                .append(CODE, object.getCode())
                .append(START_POS, placeMapper.mapToDocument(object.getStartLocation()))
                .append(FINAL_POS, placeMapper.mapToDocument(object.getEndLocation()))
                .append(OFFICIAL_ETA, object.getOfficialEta())
                .append(LOCATIONS, object.getLocations().stream()
                        .map(placeMapper::mapToDocument).collect(toList()))
                .append(CLASSIFICATION, object.getClassification().toString())
                .append(COUNTRY, object.getCountry())
                .append(SECTION_CARED_BY, object.getMaintainingSection())
                .append(LAST_UPDATE_DATE, new Date())
                .append(VARIANT, object.isVariant())
                .append(TERRITORIAL_CARED_BY, object.getTerritorialDivision())
                .append(STATS_METADATA, statsTrailMapper.mapToDocument(object.getStatsTrailMetadata()))
                .append(COORDINATES, object.getCoordinates().stream()
                        .map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(COORDINATES_LOW, object.getCoordinatesLow().stream()
                        .map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(COORDINATES_MEDIUM, object.getCoordinatesMedium().stream()
                        .map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(COORDINATES_HIGH, object.getCoordinatesHigh().stream()
                        .map(trailCoordinatesMapper::mapToDocument).collect(toList()))
                .append(MEDIA, object.getMediaList().stream()
                        .map(linkedMediaMapper::mapToDocument)
                        .collect(toList()))
                .append(GEO_LINE, getGeoLineValue(object))
                .append(CYCLO, cycloMapper.mapToDocument(object.getCycloDetails()))
                .append(RECORD_DETAILS, fileDetailsMapper.mapToDocument(object.getFileDetails()))
                .append(STATIC_TRAIL_DETAILS, staticTrailDetailsMapper.mapToDocument(object.getStaticTrailDetails()))
                .append(STATUS, object.getStatus().toString());
    }

    @Override
    public Trail mapToObject(final Document doc,
                             final TrailSimplifierLevel precisionLevel) {
        LOGGER.trace("mapToObject Document: {}, TrailSimplifierLevel: {} ", doc, precisionLevel);
        return builder()
                .id(doc.getString(ID))
                .name(doc.getString(NAME))
                .description(doc.getString(DESCRIPTION))
                .code(doc.getString(CODE))
                .startLocation(placeMapper.mapToObject(doc.get(START_POS, Document.class)))
                .endLocation(placeMapper.mapToObject(doc.get(FINAL_POS, Document.class)))
                .officialEta(doc.getInteger(OFFICIAL_ETA))
                .variant(doc.getBoolean(VARIANT))
                .locations(getLocations(doc))
                .classification(getClassification(doc))
                .statsTrailMetadata(getMetadata(doc.get(STATS_METADATA, Document.class)))
                .country(doc.getString(COUNTRY))
                .coordinates(getCoordinatesWithAltitude(doc, precisionLevel))
                .lastUpdate(getLastUpdateDate(doc))
                .maintainingSection(doc.getString(SECTION_CARED_BY))
                .territorialDivision(doc.getString(TERRITORIAL_CARED_BY))
                .geoLineString(getGeoLine(doc.get(GEO_LINE, Document.class)))
                .mediaList(getLinkedMediaMapper(doc))
                .cycloDetails(cycloMapper.mapToObject(doc.get(CYCLO, Document.class)))
                .fileDetails(fileDetailsMapper.mapToObject(doc.get(RECORD_DETAILS, Document.class)))
                .status(getStatus(doc))
                .build();
    }

    private Document getGeoLineValue(Trail object) {
        // Update
        if (object.getGeoLineString() == null) {
            LOGGER.debug("GeoLineString is null for Trail: {}", object);
            return geoLineMapper.mapCoordsToDocument(object.getCoordinates());
        }
        return geoLineMapper.mapToDocument(object.getGeoLineString());
    }

    protected GeoLineString getGeoLine(final Document doc) {
        return geoLineMapper.mapToObject(doc);
    }

    protected StatsTrailMetadata getMetadata(final Document doc) {
        return new StatsTrailMetadata(doc.getDouble(StatsTrailMetadata.TOTAL_RISE),
                doc.getDouble(StatsTrailMetadata.TOTAL_FALL),
                doc.getDouble(StatsTrailMetadata.ETA),
                doc.getDouble(StatsTrailMetadata.LENGTH),
                doc.getDouble(StatsTrailMetadata.HIGHEST_PLACE),
                doc.getDouble(StatsTrailMetadata.LOWEST_PLACE));
    }

    protected List<LinkedMedia> getLinkedMediaMapper(Document doc) {
        List<Document> list = doc.getList(Poi.MEDIA, Document.class);
        return list.stream().map(linkedMediaMapper::mapToObject).collect(toList());
    }

    private List<TrailCoordinates> getCoordinatesWithAltitude(final Document doc,
                                                              final TrailSimplifierLevel level) {

        final List<Document> list = doc.getList(getCoordinatesFieldName(level), Document.class);
        return list.stream().map(trailCoordinatesMapper::mapToObject).collect(toList());
    }

    protected List<PlaceRef> getLocations(final Document doc) {
        final List<Document> list = doc.getList(LOCATIONS, Document.class);
        return list.stream().map(placeMapper::mapToObject).collect(toList());
    }

    private String getCoordinatesFieldName(final TrailSimplifierLevel level) {
        LOGGER.trace("getCoordinatesFieldName TrailSimplifierLevel: {}", level);
        switch (level) {
            case LOW:
                return COORDINATES_LOW;
            case MEDIUM:
                return COORDINATES_MEDIUM;
            case HIGH:
                return COORDINATES_HIGH;
            default:
                return COORDINATES;
        }
    }

    protected Date getLastUpdateDate(Document doc) {
        return doc.getDate(LAST_UPDATE_DATE);
    }

    protected TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }

    protected TrailStatus getStatus(Document doc) {
        final String classification = doc.getString(STATUS);
        return TrailStatus.valueOf(classification);
    }

}
