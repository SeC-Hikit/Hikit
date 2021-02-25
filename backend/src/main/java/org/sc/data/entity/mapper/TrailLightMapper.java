package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.*;
import org.sc.data.entity.*;
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
                            final GeoLineMapper geoLineMapper,
                            final StatsTrailMapper statsTrailMapper) {
        super(positionMapper, trailCoordinatesMapper, geoLineMapper, statsTrailMapper);
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
                .withGeoLine(getGeoLine(doc.get(Trail.GEO_LINE, Document.class)))
                .build();
    }

    private List<TrailCoordinates> getCoordinatesWithAltitude(final Document doc) {
        final List<Document> list = doc.get(Trail.COORDINATES, List.class);
        return IntStream.range(0, list.size()).filter(IS_EVEN).mapToObj(elem ->
                trailCoordinatesMapper.mapToObject(list.get(elem))).collect(toList());
    }

}
