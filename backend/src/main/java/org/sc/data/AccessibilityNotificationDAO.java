package org.sc.data;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AccessibilityNotificationDAO {

    private final MongoCollection<Document> collection;
    private final AccessibilityNotificationMapper mapper;

    @Inject
    public AccessibilityNotificationDAO(final DataSource dataSource,
                                        final AccessibilityNotificationMapper mapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<AccessibilityNotification> getNotSolved() {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION, "")));
    }

    public List<AccessibilityNotification> getSolved() {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION, new Document("$ne", ""))));
    }

    public List<AccessibilityNotification> getByCode(String code) {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.TRAIL_CODE, code)));
    }

    public void upsert(final AccessibilityNotification accessibilityNotification) {
        final Document AccessibilityNotificationDocument = mapper.mapToDocument(accessibilityNotification);
        final String existingOrNewObjectId = accessibilityNotification.getId() == null ?
                new ObjectId().toHexString() : accessibilityNotification.getId();
        collection.replaceOne(new Document(AccessibilityNotification.OBJECT_ID,
                        existingOrNewObjectId),
                AccessibilityNotificationDocument, new ReplaceOptions().upsert(true));
    }

    public boolean delete(final String objectId) {
        return collection.deleteOne(new Document(AccessibilityNotification.OBJECT_ID, objectId)).getDeletedCount() > 0;
    }

    private List<AccessibilityNotification> toNotificationList(FindIterable<Document> documents) {
        return Lists.newArrayList(documents).stream().map(mapper::mapToObject).collect(toList());
    }
}
