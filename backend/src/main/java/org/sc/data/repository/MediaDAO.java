package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;
import org.sc.data.entity.Media;
import org.sc.data.entity.mapper.MediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Repository
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
        final Document mediaDoc = mapper.mapToDocument(media);
        final String objectId = new ObjectId().toHexString();
        final Document updateResult = collection.findOneAndReplace(
                new Document(Media.OBJECT_ID, objectId),
                mediaDoc, new FindOneAndReplaceOptions().upsert(true)
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

    public List<Media> deleteById(String id) {
        final List<Media> byId = getById(id);
        collection.deleteOne(new Document(Media.OBJECT_ID, id));
        return byId;
    }
}
