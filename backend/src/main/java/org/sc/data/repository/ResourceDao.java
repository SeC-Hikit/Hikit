package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.hikit.common.datasource.Datasource;
import org.sc.data.entity.mapper.ResourceEntryMapper;
import org.sc.data.model.ResourceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component
public class ResourceDao {

    private final MongoCollection<Document> collection;
    private final ResourceEntryMapper mapper;

    @Autowired
    public ResourceDao(final Datasource dataSource,
                       final ResourceEntryMapper resourceEntryMapper) {
        this.collection = dataSource.getDB().getCollection(ResourceEntry.COLLECTION_NAME);
        this.mapper = resourceEntryMapper;
    }

    public List<ResourceEntry> getById(final String id) {
        final FindIterable<Document> sort =
                collection.find(new Document(ResourceEntry.OBJECT_ID, id));
        return toEntries(sort);
    }

    public List<ResourceEntry> getByInstanceId(final String instanceId) {
        final FindIterable<Document> sort =
                collection.find(new Document(ResourceEntry.INSTANCE_ID, instanceId))
                        .sort(new Document(ResourceEntry.CREATED_ON, MongoUtils.DESCENDING_ORDER));

        return toEntries(sort);
    }

    public List<ResourceEntry> insert(final ResourceEntry resourceEntry) {
        final String objectId = new ObjectId().toHexString();
        resourceEntry.setId(objectId);
        final Document document = mapper.mapToDocument(resourceEntry);
        collection.insertOne(document);
        return getById(objectId);
    }

    public void delete(final String id) {
        collection.deleteOne(new Document(ResourceEntry.OBJECT_ID, id));
    }

    private List<ResourceEntry> toEntries(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(mapper::mapToObject).collect(toList());
    }
}