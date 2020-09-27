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

public class MaintenanceDAO {

    private final MongoCollection<Document> collection;
    private final MaintenanceMapper mapper;


    public MaintenanceDAO(final DataSource dataSource,
                          final MaintenanceMapper mapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityNotification.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<Maintenance> getFuture() {
        return toMaintenanceList(collection.find(new Document(AccessibilityNotification.DESCRIPTION, "")));
    }

    public List<Maintenance> getPast() {
        return toMaintenanceList(collection.find(new Document(AccessibilityNotification.DESCRIPTION, new Document("$ne", ""))));
    }

    public void upsert(final Maintenance maintenance) {
        final Document AccessibilityNotificationDocument = mapper.mapToDocument(maintenance);
        collection.updateOne(new Document(AccessibilityNotification.OBJECT_ID, maintenance.getId()),
                AccessibilityNotificationDocument, new UpdateOptions().upsert(true));
    }

    public void delete(final ObjectId objectId) {
        collection.deleteOne(new Document(AccessibilityNotification.OBJECT_ID, objectId));
    }

    private List<Maintenance> toMaintenanceList(FindIterable<Document> documents) {
        return Lists.newArrayList(documents).stream().map(mapper::mapToObject).collect(toList());
    }

}
