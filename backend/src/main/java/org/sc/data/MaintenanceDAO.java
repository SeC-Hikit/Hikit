package org.sc.data;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.common.config.DataSource;
import org.sc.common.rest.controller.Maintenance;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MaintenanceDAO {

    private final MongoCollection<Document> collection;
    private final MaintenanceMapper mapper;

    @Inject
    public MaintenanceDAO(final DataSource dataSource,
                          final MaintenanceMapper mapper) {
        this.collection = dataSource.getDB().getCollection(Maintenance.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<Maintenance> getFuture() {
        return toMaintenanceList(collection.find(new Document(Maintenance.DATE, new Document("$gt", new Date()))));
    }

    public List<Maintenance> getPast() {
        return toMaintenanceList(collection.find(new Document(Maintenance.DATE, new Document("$lt", new Date()))));
    }

    public void upsert(final Maintenance maintenance) {
        final Document MaintenanceDocument = mapper.mapToDocument(maintenance);
        final String existingOrNewObjectId = maintenance.getId() == null ?
                new ObjectId().toHexString() : maintenance.getId();
        collection.replaceOne(new Document(Maintenance.OBJECT_ID, existingOrNewObjectId),
                MaintenanceDocument, new ReplaceOptions().upsert(true));
    }

    public boolean delete(final String objectId) {
        return collection.deleteOne(new Document(Maintenance.OBJECT_ID, objectId)).getDeletedCount() > 0;
    }

    private List<Maintenance> toMaintenanceList(FindIterable<Document> documents) {
        return Lists.newArrayList(documents).stream().map(mapper::mapToObject).collect(toList());
    }

}
