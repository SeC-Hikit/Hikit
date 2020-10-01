package org.sc.data;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class AccessibilityNotificationDAO {

    private final MongoCollection<Document> collection;
    private final AccessibilityNotificationMapper mapper;


    public AccessibilityNotificationDAO(final DataSource dataSource,
                                        final AccessibilityNotificationMapper mapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<AccessibilityNotification> getNotSolved() {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.DESCRIPTION, "")));
    }

    public List<AccessibilityNotification> getSolved() {
        return toNotificationList(collection.find(new Document(AccessibilityNotification.RESOLUTION, new Document("$ne", ""))));
    }

    public void upsert(final AccessibilityNotification accessibilityNotification) {
        final Document AccessibilityNotificationDocument = mapper.mapToDocument(accessibilityNotification);
        collection.updateOne(new Document(AccessibilityNotification.OBJECT_ID, accessibilityNotification.getId()),
                AccessibilityNotificationDocument, new UpdateOptions().upsert(true));
    }

    public boolean delete(final ObjectId objectId) {
        return collection.deleteOne(new Document(AccessibilityNotification.OBJECT_ID, objectId)).getDeletedCount() > 0;
    }

    private List<AccessibilityNotification> toNotificationList(FindIterable<Document> documents) {
        return Lists.newArrayList(documents).stream().map(mapper::mapToObject).collect(toList());
    }

}
