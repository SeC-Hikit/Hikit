package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.controller.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
public class TrailLightMapper extends TrailMapper {

    public static final IntPredicate IS_EVEN = i -> i % 2 == 0;

    public TrailLightMapper(final PositionMapper positionMapper,
                            final TrailCoordinatesMapper trailCoordinatesMapper,
                            final StatsTrailMapper statsTrailMapper) {
        super(positionMapper, trailCoordinatesMapper, statsTrailMapper);
    }

    @Override
    public Trail mapToObject(final Document doc) {
        return Trail.TrailBuilder.aTrail()
                .withName(doc.getString(Trail.NAME))
                .withDescription(doc.getString(Trail.DESCRIPTION))
                .withCode(doc.getString(Trail.CODE))
                .withStartPos(getPos(doc, Trail.START_POS))
                .withFinalPos(getPos(doc, Trail.FINAL_POS))
                .withLocations(getLocations(doc))
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

    private List<TrailCoordinates> getCoordinatesWithAltitude(final Document doc) {
        final List<Document> list = doc.get(Trail.COORDINATES, List.class);
        return IntStream.range(0, list.size()).filter(IS_EVEN).mapToObj(elem ->
                trailCoordinatesMapper.mapToObject(list.get(elem))).collect(toList());
    }

    private List<Position> getLocations(final Document doc) {
        final List<Document> list = doc.get(Trail.LOCATIONS, List.class);
        return list.stream().map(positionMapper::mapToObject).collect(toList());
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
