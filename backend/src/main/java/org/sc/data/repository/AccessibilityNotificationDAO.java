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
import org.sc.data.entity.AccessibilityNotification;
import org.sc.data.entity.AccessibilityUnresolved;
import org.sc.data.entity.mapper.AccessibilityNotificationMapper;
import org.sc.data.entity.mapper.AccessibilityNotificationUnresolvedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Repository
public class AccessibilityNotificationDAO {

    public static final String EXISTS_PARAM = "$exists";

    private final MongoCollection<Document> collection;

    private final AccessibilityNotificationMapper mapper;
    private final AccessibilityNotificationUnresolvedMapper unresolvedMapper;

    @Autowired
    public AccessibilityNotificationDAO(final DataSource dataSource,
                                        final AccessibilityNotificationMapper mapper,
                                        final AccessibilityNotificationUnresolvedMapper unresolvedMapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
        this.unresolvedMapper = unresolvedMapper;
    }

    public List<AccessibilityUnresolved> getUnresolved(final int from,
                                                          final int to) {
        return toUnresolvedNotificationList(collection.find(
                new Document(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false)))
                .skip(from)
                .limit(to));
    }

    public List<AccessibilityUnresolved> getUnresolvedByCode(final String code) {
        return toUnresolvedNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_CODE, code)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, false))));
    }

    public List<AccessibilityNotification> getResolvedByCode(final String code) {
        return toNotificationList(collection.find(
                new Document(AccessibilityNotification.TRAIL_CODE, code)
                        .append(AccessibilityNotification.RESOLUTION, new Document(EXISTS_PARAM, true))));
    }

    public List<AccessibilityNotification> getSolved(final int from,
                                                     final int to) {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION,
                new Document(EXISTS_PARAM, true))).skip(from).limit(to));
    }

    public List<AccessibilityUnresolved> insert(final AccessibilityNotificationCreationDto accessibilityNotification) {
        final Document accessibilityNotificationDocument = mapper.mapCreationToDocument(accessibilityNotification);
        final Document addedResult = collection.findOneAndReplace(
                new Document(), accessibilityNotificationDocument,
                new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (addedResult != null) {
            return Collections.singletonList(unresolvedMapper.mapToObject(addedResult));
        }
        throw new IllegalStateException();
    }

    public List<AccessibilityNotification> resolve(final AccessibilityNotificationResolutionDto accessibilityNotificationResolutionDto) {
        collection.updateOne(new Document(AccessibilityNotification.OBJECT_ID, new ObjectId(accessibilityNotificationResolutionDto.getId())),
                new Document("$set", new Document(AccessibilityNotification.RESOLUTION, accessibilityNotificationResolutionDto.getResolution())
                        .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotificationResolutionDto.getResolutionDate())));
        return getById(accessibilityNotificationResolutionDto.getId());
    }

    public List<AccessibilityNotification> delete(final String objectId) {
        final List<AccessibilityNotification> accessibilityNotification = getById(objectId);
        collection.deleteOne(new Document(AccessibilityNotification.OBJECT_ID, new ObjectId(objectId)));
        return accessibilityNotification;
    }

    public AccessibilityNotification deleteByCode(final String code) {
        final AccessibilityNotification accessibilityNotification = getByCode(code);
        collection.deleteOne(new Document(AccessibilityNotification.TRAIL_CODE, code));
        return accessibilityNotification;
    }

    private List<AccessibilityNotification> getById(final String objectId) {
        return new ArrayList<>(toNotificationList(collection.find(
                new Document(AccessibilityNotification.OBJECT_ID, new ObjectId(objectId)))));
    }

    private AccessibilityNotification getByCode(final String code) {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.TRAIL_CODE, code))).stream().findFirst().orElse(null);
    }

    private List<AccessibilityNotification> toNotificationList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(mapper::mapToObject).collect(toList());
    }

    private List<AccessibilityUnresolved> toUnresolvedNotificationList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(unresolvedMapper::mapToObject).collect(toList());
    }


}
