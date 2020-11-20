package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.controller.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class TrailMapper implements Mapper<Trail> {

    private final PositionMapper positionMapper;
    private final CoordinatesAltitudeMapper coordinatesAltitudeMapper;
    private final StatsTrailMapper statsTrailMapper;

    @Autowired
    public TrailMapper(PositionMapper positionMapper,
                       CoordinatesAltitudeMapper coordinatesAltitudeMapper,
                       StatsTrailMapper statsTrailMapper) {
        this.positionMapper = positionMapper;
        this.coordinatesAltitudeMapper = coordinatesAltitudeMapper;
        this.statsTrailMapper = statsTrailMapper;
    }

    @Override
    public Trail mapToObject(final Document doc) {
        return Trail.TrailBuilder.aTrail()
                .withName(doc.getString(Trail.NAME))
                .withDescription(doc.getString(Trail.DESCRIPTION))
                .withCode(doc.getString(Trail.CODE))
                .withStartPos(getPos(doc, Trail.START_POS))
                .withFinalPos(getPos(doc, Trail.FINAL_POS))
                .withClassification(getClassification(doc))
                .withTrailMetadata(getMetadata(doc.get(Trail.STATS_METADATA, Document.class)))
                .withCountry(doc.getString(Trail.COUNTRY))
                .withCoordinates(getCoordinatesWithAltitude(doc))
                .withDate(getLastUpdateDate(doc))
                .withMaintainingSection(doc.getString(Trail.SECTION_CARED_BY))
                .build();
    }

    private StatsTrailMetadata getMetadata(final Document doc) {
        return new StatsTrailMetadata(doc.getDouble(StatsTrailMetadata.TOTAL_RISE),
                doc.getDouble(StatsTrailMetadata.TOTAL_FALL),
                doc.getDouble(StatsTrailMetadata.ETA),
                doc.getDouble(StatsTrailMetadata.LENGTH));
    }

    @Override
    public Document mapToDocument(final Trail object) {
        return new Document(
                Trail.NAME, object.getName())
                .append(Trail.DESCRIPTION, object.getDescription())
                .append(Trail.CODE, object.getCode())
                .append(Trail.START_POS, positionMapper.mapToDocument(object.getStartPos()))
                .append(Trail.FINAL_POS, positionMapper.mapToDocument(object.getFinalPos()))
                .append(Trail.CLASSIFICATION, object.getClassification().toString())
                .append(Trail.COUNTRY, object.getCountry())
                .append(Trail.SECTION_CARED_BY, object.getMaintainingSection())
                .append(Trail.LAST_UPDATE_DATE, new Date())
                .append(Trail.STATS_METADATA, statsTrailMapper.mapToDocument(object.getStatsMetadata()))
                .append(Trail.COORDINATES, object.getCoordinates()
                        .stream().map(coordinatesAltitudeMapper::mapToDocument).collect(toList()));
    }

    private List<CoordinatesWithAltitude> getCoordinatesWithAltitude(final Document doc) {
        final List<Document> list = doc.get(Trail.COORDINATES, List.class);
        return list.stream().map(coordinatesAltitudeMapper::mapToObject).collect(toList());
    }


    private Position getPos(final Document doc,
                            final String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return positionMapper.mapToObject(pos);
    }

    private Date getLastUpdateDate(Document doc) {
        return doc.getDate(Trail.LAST_UPDATE_DATE);
    }

    private TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }

}
