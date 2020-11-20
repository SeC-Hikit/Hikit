package org.sc.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.common.config.DataSource;
import org.sc.common.rest.controller.Maintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Repository
public class MaintenanceDAO {

    private final MongoCollection<Document> collection;
    private final MaintenanceMapper mapper;

    @Autowired
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

    public List<Maintenance> getPast(int from, int to) {
        return toMaintenanceList(collection.find(new Document(Maintenance.DATE, ""))
                .sort(new Document(Maintenance.DATE, -1)).skip(from).limit(to));
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
        return StreamSupport.stream(documents.spliterator(), false)
                .collect(Collectors.toList())
                .stream().map(mapper::mapToObject).collect(toList());
    }

}
