package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.TrailRawMapper;
import org.sc.data.model.FileDetails;
import org.sc.data.model.TrailRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.repository.MongoUtils.UPSERT_OPTIONS;

@Repository
public class TrailRawDAO {
    private static final Logger LOGGER = getLogger(TrailRawDAO.class);
    private static final String DB_REALM_STRUCTURE_SELECTOR = TrailRaw.FILE_DETAILS + "." + FileDetails.REALM;

    private final MongoCollection<Document> collection;
    private final TrailRawMapper trailRawMapper;

    @Autowired
    public TrailRawDAO(final DataSource dataSource,
                       final TrailRawMapper placeMapper) {
        this.collection = dataSource.getDB().getCollection(TrailRaw.COLLECTION_NAME);
        this.trailRawMapper = placeMapper;
    }

    public List<TrailRaw> get(final int skip, final int limit, final String realm) {
        final Document realmFilter = MongoUtils.getRealmConditionalFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        return toTrailRawList(collection.find(new Document(realmFilter)).skip(skip).limit(limit).sort(
                new Document(TrailRaw.FILE_DETAILS + "." + FileDetails.UPLOADED_ON, -1)));
    }

    public List<TrailRaw> getById(final String id) {
        return toTrailRawList(collection.find(new Document(TrailRaw.ID, id)));
    }

    public List<TrailRaw> deleteById(final String id) {
        final List<TrailRaw> byIdInMemory = getById(id);
        collection.deleteOne(new Document(TrailRaw.ID, id));
        LOGGER.info("delete TrailRaws with ID: {}", id);
        return byIdInMemory;
    }

    public List<TrailRaw> createRawTrail(final TrailRaw rawTrail) {
        final Document trailRawDoc = trailRawMapper.mapToDocument(rawTrail);
        final String objectId = new ObjectId().toHexString();
        trailRawDoc.append(TrailRaw.ID, objectId);
        Document upserted = collection.findOneAndReplace(new Document(TrailRaw.ID, objectId),
                trailRawDoc, UPSERT_OPTIONS);
        if(upserted == null) {
            LOGGER.error("createRawTrail upserted is null for TrailRaw: {}", rawTrail);
            throw new IllegalStateException();
        }
        return Collections.singletonList(trailRawMapper.mapToObject(upserted));
    }

    public long count(final String realm) {
        final Document realmFilter = MongoUtils.getRealmConditionalFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        return collection.countDocuments(realmFilter);
    }

    private List<TrailRaw> toTrailRawList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailRawMapper::mapToObject).collect(toList());
    }
}
