package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sc.configuration.DataSource;
import org.sc.data.model.Media;
import org.sc.data.entity.mapper.MediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

@Repository
public class MediaDAO {
    private static final Logger LOGGER = getLogger(MediaDAO.class);

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
        LOGGER.error("save updateResult is null for Media: {}", media);
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
        LOGGER.info("deleteById Medias: {}, for id: {}", byId, id);
        return byId;
    }

    public long count(){
        return collection.countDocuments();
    }
}
