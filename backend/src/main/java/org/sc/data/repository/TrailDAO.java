package org.sc.data.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.*;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static java.util.stream.Collectors.toList;
import static org.sc.data.repository.MongoConstants.*;

@Repository
public class TrailDAO {

    private static final String RESOLVED_START_POS_COORDINATE = Trail.START_POS + "." + Place.COORDINATES;
    public static final String PLACE_ID_IN_LOCATIONS = Trail.LOCATIONS + DOT + PlaceRef.PLACE_ID;


    private final MongoCollection<Document> collection;

    private final Mapper<Trail> trailMapper;
    private final LinkedMediaMapper linkedMediaMapper;
    private final Mapper<Trail> trailLightMapper;
    private final Mapper<TrailPreview> trailPreviewMapper;
    private final PlaceRefMapper placeRefMapper;

    @Autowired
    public TrailDAO(final DataSource dataSource,
                    final TrailMapper trailMapper,
                    final LinkedMediaMapper linkedMediaMapper,
                    final TrailLightMapper trailLightMapper,
                    final TrailPreviewMapper trailPreviewMapper,
                    final PlaceRefMapper placeRefMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);
        this.trailMapper = trailMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.trailLightMapper = trailLightMapper;
        this.trailPreviewMapper = trailPreviewMapper;
        this.placeRefMapper = placeRefMapper;
    }

    public List<Trail> getTrailsByStartPosMetricDistance(final double longitude,
                                                         final double latitude,
                                                         final double meters,
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
    public List<Trail> trailsByPointDistance(double longitude, double latitude, double meters, int limit) {
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

    public List<Trail> getTrails(boolean isLight, int page, int count) {
        if (isLight) {
            return toTrailsLightList(collection.find(new Document()).skip(page).limit(count));
        }
        return toTrailsList(collection.find(new Document()).skip(page).limit(count));
    }

    public List<Trail> getTrailById(String id, boolean isLight) {
        if (isLight) {
            return toTrailsLightList(collection.find(new Document(Trail.ID, id)));
        }
        return toTrailsList(collection.find(new Document(Trail.ID, id)));
    }

    public List<Trail> getTrailByPlaceId(String id, boolean isLight) {
        if (isLight) {
            return toTrailsLightList(collection.find(new Document(PLACE_ID_IN_LOCATIONS, id)));
        }
        return toTrailsList(collection.find(new Document(PLACE_ID_IN_LOCATIONS, id)));
    }

    public List<Trail> delete(final String id) {
        List<Trail> trailByCode = getTrailById(id, false);
        collection.deleteOne(new Document(Trail.ID, id));
        return trailByCode;
    }

    public List<Trail> upsert(final Trail trailRequest) {
        final String existingOrNewObjectId = trailRequest.getId() == null ?
                new ObjectId().toHexString() : trailRequest.getId();
        final Document trailDocument = trailMapper.mapToDocument(trailRequest)
                .append(Trail.ID, existingOrNewObjectId);
        final Document updateResult = collection.findOneAndReplace(
                new Document(Trail.ID, existingOrNewObjectId),
                trailDocument, UPSERT_OPTIONS);
        if (updateResult == null) {
            throw new IllegalStateException();
        }
        return Collections.singletonList(trailMapper.mapToObject(updateResult));
    }

    public List<TrailPreview> getTrailPreviews(final int page, final int count) {
        final Bson project = getTrailPreviewProjection();

        final Bson limit = Aggregates.limit(count);
        final Bson skip = Aggregates.skip(page);

        return toTrailsPreviewList(collection.aggregate(Arrays.asList(project, limit, skip)));
    }

    public List<TrailPreview> trailPreviewById(final String id) {
        final Bson project = getTrailPreviewProjection();
        final Bson equalId = match(eq(Trail.ID, id));
        return toTrailsPreviewList(collection.aggregate(Arrays.asList(equalId, project)));
    }

    public void unlinkMediaByAllTrails(final String mediaId) {
        // E.g: db.core.test.update({"b.mediaId": 1}, { $pull : { "b.$.mediaId": 1}}, {multi: true})
        collection.updateMany(new Document(),
                new Document(PULL, new Document((Trail.MEDIA),
                        new Document(LinkedMedia.ID, mediaId))));
    }

    public List<Trail> linkMedia(final String id,
                                 final LinkedMedia linkMedia) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document(ADD_TO_SET, new Document(Trail.MEDIA,
                        linkedMediaMapper.mapToDocument(linkMedia))));
        return getTrailById(id, true);
    }

    public List<Trail> unlinkMedia(final String id,
                                   final String mediaId) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document(PULL, new Document(Trail.MEDIA,
                        new Document(LinkedMedia.ID, mediaId))));
        return getTrailById(id, true);
    }

    private Bson getTrailPreviewProjection() {
        Bson project = project(fields(
                include(Trail.CLASSIFICATION),
                include(Trail.LAST_UPDATE_DATE),
                include(Trail.CODE),
                computed(Trail.START_POS,
                        new Document("$arrayElemAt",
                                Arrays.asList(DOLLAR + Trail.LOCATIONS, 0))),
                computed(Trail.FINAL_POS,
                        new Document("$arrayElemAt",
                                Arrays.asList(DOLLAR + Trail.LOCATIONS, -1)))
        ));
        BsonDocument bsonDocument = project.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
        System.out.println(bsonDocument);

        return project;
    }

    public List<Trail> linkPlace(String id, PlaceRef placeRef) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document(ADD_TO_SET, new Document(Trail.LOCATIONS,
                        placeRefMapper.mapToDocument(placeRef))));
        return getTrailById(id, false);
    }

    public List<Trail> unLinkPlace(String id, PlaceRef placeRef) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document(PULL, new Document(Trail.LOCATIONS,
                        new Document(PlaceRef.PLACE_ID, placeRef.getPlaceId()))));
        return getTrailById(id, false);
    }

    public void unlinkPlaceFromAllTrails(String placeId) {
        Document update = new Document(PULL, new Document(Trail.LOCATIONS,
                new Document(PlaceRef.PLACE_ID, placeId)));
        collection.updateMany(new Document(),
                update);
    }

    public long countTrail() {
        return collection.countDocuments();
    }

    private List<TrailPreview> toTrailsPreviewList(final AggregateIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailPreviewMapper::mapToObject).collect(toList());
    }

    private List<Trail> toTrailsLightList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailLightMapper::mapToObject).collect(toList());
    }

    private List<Trail> toTrailsList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailMapper::mapToObject).collect(toList());
    }

}