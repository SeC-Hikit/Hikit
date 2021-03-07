package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.stereotype.Component;

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
                            final StatsTrailMapper statsTrailMapper,
                            final LinkedMediaMapper linkedMediaMapper) {
        super(positionMapper, trailCoordinatesMapper, geoLineMapper, statsTrailMapper, linkedMediaMapper);
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



    private List<TrailCoordinates> getCoordinatesWithAltitude(final Document doc) {
        final List<Document> list = doc.getList(Trail.COORDINATES, Document.class);
        return IntStream.range(0, list.size()).filter(IS_EVEN).mapToObj(elem ->
                trailCoordinatesMapper.mapToObject(list.get(elem))).collect(toList());
    }

}
