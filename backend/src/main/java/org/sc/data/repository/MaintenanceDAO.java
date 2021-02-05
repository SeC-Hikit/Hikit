package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
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

    public List<Maintenance> getPastForTrailCode(final String code,
                                                 final int from,
                                                 final int to) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.DATE, new Document("$lt", new Date()))
                        .append(Maintenance.TRAIL_CODE, code))
                .sort(new Document(Maintenance.DATE, -1))
                .skip(from).limit(to));
    }

    public List<Maintenance> upsert(final Maintenance maintenance) {
        final Document maintenanceDocument = mapper.mapToDocument(maintenance);
        final String existingOrNewObjectId = maintenance.get_id() == null ?
                new ObjectId().toHexString() : maintenance.get_id();
        final Document updateResult = collection.findOneAndReplace(
                new Document(Maintenance.OBJECT_ID, existingOrNewObjectId),
                maintenanceDocument, new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (updateResult != null) {
            return Collections.singletonList(mapper.mapToObject(updateResult));
        }
        throw new IllegalStateException();
    }

    public List<Maintenance> delete(final String objectId) {
        final Maintenance byId = getById(objectId);
        collection.deleteOne(new Document(Maintenance.OBJECT_ID, objectId));
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
