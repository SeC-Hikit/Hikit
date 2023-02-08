package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.hikit.common.datasource.Datasource;
import org.sc.data.entity.mapper.MaintenanceMapper;
import org.sc.data.model.FileDetails;
import org.sc.data.model.Maintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

@Repository
public class MaintenanceDAO {
    private static final Logger LOGGER = getLogger(MaintenanceDAO.class);
    public static final String DB_REALM_STRUCTURE_SELECTOR = Maintenance.RECORD_DETAILS + "." + FileDetails.REALM;

    private final MongoCollection<Document> collection;
    private final MaintenanceMapper mapper;

    @Autowired
    public MaintenanceDAO(final Datasource dataSource,
                          final MaintenanceMapper mapper) {
        this.collection = dataSource.getDB().getCollection(Maintenance.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<Maintenance> getFuture(final int from,
                                       final int to,
                                       final LocalDate date,
                                       final String realm) {
        return toMaintenanceList(collection.find(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Maintenance.DATE, new Document("$gt", date)))
                .skip(from).limit(to));
    }

    public List<Maintenance> getPastDate(final int from,
                                         final int to,
                                         final LocalDate date,
                                         final String realm) {
        return toMaintenanceList(collection.find(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Maintenance.DATE, new Document("$lt", date)))
                .sort(new Document(Maintenance.DATE, -1))
                .skip(from).limit(to));
    }

    public List<Maintenance> getPastForTrailCode(final String trailId,
                                                 final int from,
                                                 final int to,
                                                 final LocalDate date) {
        return toMaintenanceList(collection.find(
                        new Document(Maintenance.DATE, new Document("$lt", date))
                                .append(Maintenance.TRAIL_ID, trailId))
                .sort(new Document(Maintenance.DATE, -1))
                .skip(from).limit(to));
    }

    public List<Maintenance> upsert(final Maintenance maintenance) {
        final Document maintenanceDocument = mapper.mapToDocument(maintenance);
        final String existingOrNewObjectId = maintenance.getId() == null ?
                new ObjectId().toHexString() : maintenance.getId();
        final Document updateResult = collection.findOneAndReplace(
                new Document(Maintenance.OBJECT_ID, existingOrNewObjectId),
                maintenanceDocument, new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (updateResult != null) {
            return Collections.singletonList(mapper.mapToObject(updateResult));
        }
        LOGGER.error("upsert addedResult is null for Maintenance: {}", maintenance);
        throw new IllegalStateException();
    }

    public List<Maintenance> delete(final String objectId) {
        final List<Maintenance> byId = getById(objectId);
        LOGGER.info("delete Maintenances: {}, for id: {}", byId, objectId);
        if (byId.isEmpty()) {
            return Collections.emptyList();
        }
        byId.forEach(maintenance -> collection.deleteOne(new Document(Maintenance.OBJECT_ID, maintenance.getId())));
        return byId;
    }

    public List<Maintenance> deleteByTrailId(final String trailId) {
        final List<Maintenance> byId = getByTrailId(trailId);
        collection.deleteMany(new Document(Maintenance.TRAIL_ID, trailId));
        LOGGER.info("deleteByTrailId Maintenances: {}, for id: {}", byId, trailId);
        return byId;
    }

    public List<Maintenance> getById(final String id) {
        return toMaintenanceList(collection.find(
                new Document(Maintenance.OBJECT_ID, id)));
    }

    public List<Maintenance> getByTrailId(final String trailId) {
        return new ArrayList<>(toMaintenanceList(collection.find(
                new Document(Maintenance.TRAIL_ID, trailId))));
    }

    public long countMaintenance(String realm) {
        return collection.countDocuments(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
        );
    }

    public long countPastMaintenance(String realm) {
        return collection.countDocuments(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Maintenance.DATE, new Document("$lt", new Date())));
    }

    public long countFutureMaintenance(String realm) {
        return collection.countDocuments(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Maintenance.DATE, new Document("$gt", new Date())));
    }

    private List<Maintenance> toMaintenanceList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .collect(Collectors.toList())
                .stream().map(mapper::mapToObject).collect(toList());
    }
}
