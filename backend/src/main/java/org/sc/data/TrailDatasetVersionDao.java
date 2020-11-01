package org.sc.data;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sc.configuration.DataSource;

import javax.inject.Inject;
import java.util.Date;
import java.util.Objects;

public class TrailDatasetVersionDao {

    public static final String CORE_TRAIL_DATASET_VERSION = "core.TrailDatasetVersion";
    public static final int DESCENDING_ORDER = -1;

    private final MongoCollection<Document> collection;
    private final TrailDatasetMapper mapper;

    @Inject
    public TrailDatasetVersionDao(final DataSource dataSource,
                                  final TrailDatasetMapper mapper) {
        this.collection = dataSource.getDB().getCollection(CORE_TRAIL_DATASET_VERSION);
        this.mapper = mapper;
    }

    public TrailDatasetVersion getLast() {
        if(collection.countDocuments() == 0){
            return new TrailDatasetVersion(0, new Date());
        }
        return mapper.mapToObject(
                Objects.requireNonNull(collection.find(new Document())
                        .sort(new Document(TrailDatasetMapper.VERSION_FIELD, DESCENDING_ORDER)).first()));
    }

    public void addVersion() {
        long collectionSize = collection.countDocuments();
        long nextVersion = collectionSize + 1;
        collection.insertOne(mapper.mapToDocument(new TrailDatasetVersion(nextVersion, new Date())));
    }



}