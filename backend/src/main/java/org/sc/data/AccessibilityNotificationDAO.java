package org.sc.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.common.rest.AccessibilityNotification;
import org.sc.common.rest.AccessibilityNotificationCreation;
import org.sc.common.rest.AccessibilityNotificationResolution;
import org.sc.common.rest.AccessibilityNotificationUnresolved;
import org.sc.configuration.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Repository
public class AccessibilityNotificationDAO {

    public static final String EXISTS_PARAM = "$exists";

    private final MongoCollection<Document> collection;

    private final AccessibilityNotificationMapper mapper;
    private final AccessibilityNotificationUnresolvedMapper unresolvedMapper;
    private final AccessibilityNotificationCreationMapper mapperCreation;

    @Autowired
    public AccessibilityNotificationDAO(final DataSource dataSource,
                                        final AccessibilityNotificationMapper mapper,
                                        final AccessibilityNotificationUnresolvedMapper unresolvedMapper,
                                        final AccessibilityNotificationCreationMapper mapperCreation) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
        this.unresolvedMapper = unresolvedMapper;
        this.mapperCreation = mapperCreation;
    }

    public List<AccessibilityNotificationUnresolved> getNotSolved(final int from,
                                                                  final int to) {
        return toUnresolvedNotificationList(collection.find(
                new Document(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false)))
                .skip(from)
                .limit(to));
    }

    public List<AccessibilityNotificationUnresolved> getUnresolvedByCode(String code) {
        return toUnresolvedNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_CODE, code)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false))));
    }

    public List<AccessibilityNotification> getResolvedByCode(String code) {
        return toNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_CODE, code)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, true))));
    }

    public List<AccessibilityNotification> getSolved(final int from,
                                                     final int to) {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION,
                new Document(EXISTS_PARAM, true))).skip(from).limit(to));
    }

    public boolean upsert(final AccessibilityNotificationCreation accessibilityNotification) {
        final Document accessibilityNotificationDocument = mapperCreation.mapToDocument(accessibilityNotification);
        return collection.replaceOne(new Document(AccessibilityNotification.OBJECT_ID,
                new ObjectId().toHexString()), accessibilityNotificationDocument, new ReplaceOptions().upsert(true)).getUpsertedId() != null;
    }

    public boolean resolve(final AccessibilityNotificationResolution accessibilityNotificationResolution) {
        return collection.updateOne(new Document(AccessibilityNotification.OBJECT_ID, accessibilityNotificationResolution.get_id()),
                new Document("$set", new Document(AccessibilityNotification.RESOLUTION, accessibilityNotificationResolution.getResolution())
                        .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotificationResolution.getResolutionDate()))).getModifiedCount() > 0;
    }

    public boolean delete(final String objectId) {
        return collection.deleteOne(new Document(AccessibilityNotification.OBJECT_ID, objectId)).getDeletedCount() > 0;
    }

    public boolean deleteByCode(final String code) {
        return collection.deleteOne(new Document(AccessibilityNotification.TRAIL_CODE, code)).getDeletedCount() > 0;
    }

    private List<AccessibilityNotification> toNotificationList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(mapper::mapToObject).collect(toList());
    }

    private List<AccessibilityNotificationUnresolved> toUnresolvedNotificationList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(unresolvedMapper::mapToObject).collect(toList());
    }


}
