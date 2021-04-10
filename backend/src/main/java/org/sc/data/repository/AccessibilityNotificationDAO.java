package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.common.rest.AccessibilityNotificationCreationDto;
import org.sc.common.rest.AccessibilityNotificationResolutionDto;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.AccessibilityNotificationMapper;
import org.sc.data.model.AccessibilityNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.sc.data.repository.MongoConstants.EXISTS_PARAM;

@Repository
public class AccessibilityNotificationDAO {

    private final MongoCollection<Document> collection;

    private final AccessibilityNotificationMapper mapper;

    @Autowired
    public AccessibilityNotificationDAO(final DataSource dataSource,
                                        final AccessibilityNotificationMapper mapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<AccessibilityNotification> getUnresolved(final int skip,
                                                         final int limit) {
        return toNotificationList(collection.find(
                new Document(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false)))
                .skip(skip)
                .limit(limit));
    }

    public List<AccessibilityNotification> getUnresolvedByTrailId(final String id, final int skip, final int limit) {
        return toNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_ID, id)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false)))
                .skip(skip).limit(limit));
    }

    public List<AccessibilityNotification> getResolvedByTrailId(final String id, final int skip, final int limit) {
        return toNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_ID, id)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, true)))
                .skip(skip).limit(limit));
    }

    public List<AccessibilityNotification> getSolved(final int skip,
                                                     final int limit) {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION,
                new Document(EXISTS_PARAM, true))).skip(skip).limit(limit));
    }

    public List<AccessibilityNotification> getByTrailId(final String trailId) {
        return new ArrayList<>(toNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_ID, new ObjectId(trailId)))));
    }

    public List<AccessibilityNotification> deleteByTrailId(final String trailId) {
        List<AccessibilityNotification> byTrailId = getByTrailId(trailId);
        collection.deleteMany(new Document(AccessibilityNotification.TRAIL_ID, new ObjectId(trailId)));
        return byTrailId;
    }

    public List<AccessibilityNotification> insert(final AccessibilityNotificationCreationDto accessibilityNotification) {
        final Document accessibilityNotificationDocument = mapper.mapCreationToDocument(accessibilityNotification);
        final Document addedResult = collection.findOneAndReplace(
                new Document(), accessibilityNotificationDocument,
                new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (addedResult != null) {
            return Collections.singletonList(mapper.mapToObject(addedResult));
        }
        throw new IllegalStateException();
    }

    public List<AccessibilityNotification> resolve(final AccessibilityNotificationResolutionDto accessibilityNotificationResolutionDto) {
        collection.updateOne(
                new Document(AccessibilityNotification.ID, new ObjectId(accessibilityNotificationResolutionDto.getId())),
                new Document("$set", new Document(AccessibilityNotification.RESOLUTION, accessibilityNotificationResolutionDto.getResolution())
                        .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotificationResolutionDto.getResolutionDate())));
        return getById(accessibilityNotificationResolutionDto.getId());
    }

    public List<AccessibilityNotification> delete(final String objectId) {
        final List<AccessibilityNotification> accessibilityNotification = getById(objectId);
        collection.deleteOne(new Document(AccessibilityNotification.ID, new ObjectId(objectId)));
        return accessibilityNotification;
    }

    private List<AccessibilityNotification> getById(final String objectId) {
        return new ArrayList<>(toNotificationList(collection.find(
                new Document(AccessibilityNotification.ID, new ObjectId(objectId)))));
    }

    private List<AccessibilityNotification> toNotificationList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(mapper::mapToObject).collect(toList());
    }

    public long countAccessibility() {
        return collection.countDocuments();
    }

    public long countSolved() {
        return collection.countDocuments(new Document(AccessibilityNotification.RESOLUTION,
                new Document(EXISTS_PARAM, true)));
    }

    public long countNotSolved() {
        return collection.countDocuments(new Document(AccessibilityNotification.RESOLUTION,
                new Document(EXISTS_PARAM, false)));
    }

    public long countSolvedForTrailId(final String trailId) {
        return collection.countDocuments(new Document(AccessibilityNotification.TRAIL_ID, trailId)
                .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, true)));
    }

    public long countNotSolvedForTrailId(final String trailId) {
        return collection.countDocuments(new Document(AccessibilityNotification.TRAIL_ID, trailId)
                .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false)));
    }
}
