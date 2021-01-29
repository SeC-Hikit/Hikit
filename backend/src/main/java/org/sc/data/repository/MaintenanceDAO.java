package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;
import org.sc.data.entity.Maintenance;
import org.sc.data.entity.mapper.MaintenanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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

    public List<Maintenance> getFuture(final int from,
                                       final int to) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.DATE, new Document("$gt", new Date())))
                .skip(from).limit(to));
    }

    public List<Maintenance> getPast(final int from,
                                     final int to) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.DATE, new Document("$lt", new Date())))
                .sort(new Document(Maintenance.DATE, -1))
                .skip(from).limit(to));
    }

    public List<Maintenance> upsert(final Maintenance maintenance) {
        final Document MaintenanceDocument = mapper.mapToDocument(maintenance);
        final String existingOrNewObjectId = maintenance.get_id() == null ?
                new ObjectId().toHexString() : maintenance.get_id();
        UpdateResult updateResult = collection.replaceOne(new Document(Maintenance.OBJECT_ID, existingOrNewObjectId),
                MaintenanceDocument, new ReplaceOptions().upsert(true));
        return Collections.singletonList(getById(updateResult.getUpsertedId().asString().toString()));
    }

    public List<Maintenance> delete(final String id) {
        final Maintenance byId = getById(id);
        collection.deleteOne(new Document(Maintenance.OBJECT_ID, id));
        return Collections.singletonList(byId);
    }

    public List<Maintenance> deleteByCode(final String trailCode) {
        final Maintenance byCode = getByTrailId(trailCode);
        collection.deleteOne(new Document(Maintenance.TRAIL_CODE, trailCode));
        return Collections.singletonList(byCode);
    }

    private Maintenance getById(final String _id) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.OBJECT_ID, _id)))
                .stream()
                .findFirst().orElse(null);
    }

    private Maintenance getByTrailId(final String trailId) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.TRAIL_CODE, trailId)))
                .stream()
                .findFirst().orElse(null);
    }

    private List<Maintenance> toMaintenanceList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .collect(Collectors.toList())
                .stream().map(mapper::mapToObject).collect(toList());
    }

}
