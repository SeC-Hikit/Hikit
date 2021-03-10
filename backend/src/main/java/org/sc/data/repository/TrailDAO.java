package org.sc.data.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.*;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.sc.data.repository.MongoConstants.*;

@Repository
public class TrailDAO {

    private static final String RESOLVED_START_POS_COORDINATE = Trail.START_POS + "." + Position.COORDINATES;

    private final MongoCollection<Document> collection;

    private final Mapper<Trail> trailMapper;
    private final LinkedMediaMapper linkedMediaMapper;
    private final Mapper<Trail> trailLightMapper;
    private final Mapper<TrailPreview> trailPreviewMapper;

    @Autowired
    public TrailDAO(final DataSource dataSource,
                    final TrailMapper trailMapper,
                    final LinkedMediaMapper linkedMediaMapper,
                    final TrailLightMapper trailLightMapper,
                    final TrailPreviewMapper trailPreviewMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);
        this.trailMapper = trailMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.trailLightMapper = trailLightMapper;
        this.trailPreviewMapper = trailPreviewMapper;
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

    public List<Trail> getTrailByCode(String code, boolean isLight) {
        if (isLight) {
            return toTrailsLightList(collection.find(new Document(Trail.CODE, code)));
        }
        return toTrailsList(collection.find(new Document(Trail.CODE, code)));
    }

    public List<Trail> delete(final String code) {
        List<Trail> trailByCode = getTrailByCode(code, false);
        collection.deleteOne(new Document(Trail.CODE, code));
        return trailByCode;
    }

    public List<Trail> upsert(final Trail trailRequest) {
        final String existingOrNewObjectId = trailRequest.getId() == null ?
                new ObjectId().toHexString() : trailRequest.getId();
        final Document trailDocument = trailMapper.mapToDocument(trailRequest)
                .append(Trail.ID, existingOrNewObjectId);
        final Document updateResult = collection.findOneAndReplace(
                new Document(Trail.ID, existingOrNewObjectId),
                trailDocument, new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (updateResult != null) {
            return Collections.singletonList(trailMapper.mapToObject(updateResult));
        }
        throw new IllegalStateException();
    }

    public List<TrailPreview> getTrailPreviews(final int page, final int count) {
        return toTrailsPreviewList(collection.find()
                .projection(projectPreview())
                .skip(page)
                .limit(count));
    }

    public List<TrailPreview> trailPreviewByCode(final String code) {
        return toTrailsPreviewList(collection.find(new Document(Trail.CODE, code))
                .projection(projectPreview()));
    }

    public void unlinkMediaByAllTrails(final String mediaId) {
        // E.g: db.core.test.update({"b.mediaId": 1}, { $pull : { "b.$.mediaId": 1}}, {multi: true})
        collection.updateMany(new Document(),
                new Document(PULL, new Document((Trail.MEDIA),
                        new Document(LinkedMedia.ID, mediaId))));
    }

    public List<Trail> linkMedia(final String code,
                                 final LinkedMedia linkMedia) {
        collection.updateOne(new Document(Trail.CODE, code),
                new Document(ADD_TO_SET, new Document(Trail.MEDIA, linkedMediaMapper.mapToDocument(linkMedia))));
        return getTrailByCode(code, true);
    }

    public List<Trail> unlinkMedia(final String code,
                                   final String mediaId) {
        collection.updateOne(new Document(Trail.CODE, code),
                new Document(PULL, new Document(Trail.MEDIA, new Document(LinkedMedia.ID, mediaId))));
        return getTrailByCode(code, true);
    }

    private List<TrailPreview> toTrailsPreviewList(final FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailPreviewMapper::mapToObject).collect(toList());
    }

    private Document projectPreview() {
        return new Document(Trail.CODE, 1)
                .append(Trail.START_POS, 1)
                .append(Trail.FINAL_POS, 1)
                .append(Trail.CLASSIFICATION, 1)
                .append(Trail.LAST_UPDATE_DATE, 1);
    }

    private List<Trail> toTrailsLightList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailLightMapper::mapToObject).collect(toList());
    }

    private List<Trail> toTrailsList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailMapper::mapToObject).collect(toList());
    }

    public long countTrail() {
        return collection.countDocuments();
    }
}