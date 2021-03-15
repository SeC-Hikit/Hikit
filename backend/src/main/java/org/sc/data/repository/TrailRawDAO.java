package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.TrailRawMapper;
import org.sc.data.model.Trail;
import org.sc.data.model.TrailRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.sc.data.repository.MongoConstants.UPSERT_OPTIONS;

@Repository
public class TrailRawDAO {

    private final MongoCollection<Document> collection;
    private final TrailRawMapper trailRawMapper;

    @Autowired
    public TrailRawDAO(final DataSource dataSource,
                       final TrailRawMapper placeMapper) {
        this.collection = dataSource.getDB().getCollection(TrailRaw.COLLECTION_NAME);
        this.trailRawMapper = placeMapper;
    }

    public List<TrailRaw> getById(final String id) {
        return toTrailRawList(collection.find(new Document(TrailRaw.ID, id)));
    }

    public List<TrailRaw> createRawTrail(final TrailRaw rawTrail) {
        final Document trailRawDoc = trailRawMapper.mapToDocument(rawTrail);
        final String objectId = new ObjectId().toHexString();
        trailRawDoc.append(TrailRaw.ID, objectId);
        Document upserted = collection.findOneAndReplace(new Document(TrailRaw.ID, objectId),
                trailRawDoc, UPSERT_OPTIONS);
        if(upserted == null) {
            throw new IllegalStateException();
        }
        return Collections.singletonList(trailRawMapper.mapToObject(upserted));
    }

    private List<TrailRaw> toTrailRawList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(trailRawMapper::mapToObject).collect(toList());
    }
}
