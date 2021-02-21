package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;
import org.sc.data.entity.Maintenance;
import org.sc.data.entity.Media;
import org.sc.data.entity.mapper.MediaMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class MediaDAO {
    private final MongoCollection<Document> collection;
    private final MediaMapper mapper;

    @Autowired
    public MediaDAO(final DataSource dataSource,
                    final MediaMapper mapper) {
        this.collection = dataSource.getDB().getCollection(Media.COLLECTION_NAME);
        this.mapper = mapper;
    }

    public List<Media> getById(final String id) {
        return toMediaList(collection.find(new Document(Media.OBJECT_ID, id)));
    }

    public List<Media> save(final Media media) {
        final Document maintenanceDocument = mapper.mapToDocument(media);
        final String objectId = new ObjectId().toHexString();
        final Document updateResult = collection.findOneAndReplace(
                new Document(Maintenance.OBJECT_ID, objectId),
                maintenanceDocument, new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (updateResult != null) {
            return Collections.singletonList(mapper.mapToObject(updateResult));
        }
        throw new IllegalStateException();
    }

    private List<Media> toMediaList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .collect(Collectors.toList())
                .stream().map(mapper::mapToObject).collect(toList());
    }

}
