package org.sc.data;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TrailDAO {

    public static final String $_NEAR_OPERATOR = "$near";
    public static final String NEAR_OPERATOR = "near";
    public static final String LIMIT = "$limit";
    public static final String RESOLVED_COORDINATES = "coordinates";
    static final String $_MAX_M_DISTANCE_FILTER = "$maxDistance";
    static final String $_MIN_DISTANCE_FILTER = "$minDistance";
    public static final String $_GEO_NEAR_OPERATOR = "$geoNear";
    public static final String DISTANCE_FIELD = "distanceField";
    public static final String KEY_FIELD = "key";
    public static final String INCLUDE_LOCS_FIELD = "includeLocs";
    public static final String MAX_DISTANCE_M = "maxDistance";
    public static final String SPHERICAL_FIELD = "spherical";
    public static final String UNIQUE_DOCS_FIELD = "uniqueDocs";

    private static final String RESOLVED_START_POS_COORDINATE = Trail.START_POS + "." + Position.LOCATION;
    private static final String RESOLVED_START_POS_POSTCODE = Trail.START_POS + "." + Position.POSTCODE;

    // Max number of documents output per request
    public static final int RESULT_LIMIT = 150;
    public static final int RESULT_LIMIT_ONE = 1;

    private final MongoCollection<Document> collection;
    private final Mapper<Trail> trailMapper;

    @Inject
    public TrailDAO(final DataSource dataSource,
                    final TrailMapper trailMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);
        this.trailMapper = trailMapper;
    }

    public Trail getTrailByCodeAndPostcodeCountry(final String postcode,
                                                  final String trailCode,
                                                  final String country) {
        final FindIterable<Document> documents = collection.find(
                new Document(Trail.COUNTRY, country)
                        .append(Trail.CODE, trailCode)
                        .append(RESOLVED_START_POS_POSTCODE, postcode))
                .limit(RESULT_LIMIT_ONE);
        return toTrailsList(documents).stream().findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public List<Trail> getTrailsByStartPosMetricDistance(final double longitude,
                                                         final double latitude,
                                                         final int meters,
                                                         final int limit) {
        final FindIterable<Document> documents = collection.find(
                new Document(RESOLVED_START_POS_COORDINATE,
                        new Document($_NEAR_OPERATOR,
                                new Document(RESOLVED_COORDINATES, Arrays.asList(longitude, latitude)
                                )
                        ).append($_MIN_DISTANCE_FILTER, 0).append($_MAX_M_DISTANCE_FILTER, meters))).limit(limit);
        return toTrailsList(documents);
    }

    @NotNull
    public List<Trail> trailsByPointDistance(double longitude, double latitude, int meters, int limit) {
        final AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(new Document($_GEO_NEAR_OPERATOR,
                        new Document(NEAR_OPERATOR, new Document("type", "Point").append("coordinates", Arrays.asList(longitude, latitude)))
                                .append(DISTANCE_FIELD, "distanceToIt")
                                .append(KEY_FIELD, "geoPoints.coordinates")
                                .append(INCLUDE_LOCS_FIELD, "closestLocation")
                                .append(MAX_DISTANCE_M, meters)
                                .append(SPHERICAL_FIELD, "true")
                                .append(UNIQUE_DOCS_FIELD, "true")),
                new Document(LIMIT, limit)
        ));
        return toTrailsList(aggregate);
    }

    @NotNull
    public List<Trail> getTrails() {
        return executeQueryAndGetResult(new Document());
    }

    @NotNull
    public List<Trail> getTrailByCode(@NotNull String code) {
        return toTrailsList(collection.find(new Document("code", code)));
    }

    public List<Trail> executeQueryAndGetResult(final Document doc) {
        return toTrailsList(collection.find(doc).limit(RESULT_LIMIT));
    }

    @NotNull
    private List<Trail> toTrailsList(Iterable<Document> documents) {
        return Lists.newArrayList(documents).stream().map(trailMapper::mapToObject).collect(toList());
    }

    public void upsertTrail(final Trail trailRequest) {
        final Document trail = trailMapper.mapToDocument(trailRequest);
        collection.updateOne(new Document(Trail.CODE, trailRequest.getCode())
                        .append(Trail.COUNTRY, trailRequest.getCountry())
                        .append(RESOLVED_START_POS_POSTCODE, trailRequest.getStartPos().getPostCode()),
                trail, new UpdateOptions().upsert(true));
    }
}