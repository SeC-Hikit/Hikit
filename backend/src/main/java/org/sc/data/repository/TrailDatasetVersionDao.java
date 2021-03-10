package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.TrailDatasetMapper;
import org.sc.data.TrailDatasetVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;

@Repository
public class TrailDatasetVersionDao {

    public static final String CORE_TRAIL_DATASET_VERSION = "core.TrailDatasetVersion";
    public static final int DESCENDING_ORDER = -1;

    private final MongoCollection<Document> collection;
    private final TrailDatasetMapper mapper;

    @Autowired
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

    public void increaseVersion() {
        long collectionSize = collection.countDocuments();
        long nextVersion = collectionSize + 1;
        collection.insertOne(mapper.mapToDocument(new TrailDatasetVersion(nextVersion, new Date())));
    }

    public long countImport() {
        return collection.countDocuments();
    }


}
