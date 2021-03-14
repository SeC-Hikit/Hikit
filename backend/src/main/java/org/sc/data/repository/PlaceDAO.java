package org.sc.data.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.PlaceMapper;
import org.sc.data.model.Place;
import org.sc.data.model.Trail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Repository
public class PlaceDAO {
    private final MongoCollection<Document> collection;
    private final PlaceMapper placeMapper;

    @Autowired
    public PlaceDAO(final DataSource dataSource,
                    final PlaceMapper placeMapper) {
        this.collection = dataSource.getDB().getCollection(Place.COLLECTION_NAME);
        this.placeMapper = placeMapper;
    }

    @NotNull
    public List<Place> get(int page, int count) {
        return toPlaceList(collection.find(new Document()).skip(page).limit(count));
    }

    public List<Place> getById(final String id) {
        return toPlaceList(collection.find(new Document(Place.ID, id)));
    }

    public List<Place> getLikeName(final String name, int page, int count) {
        Document filter = new Document(MongoConstants.OR, Arrays.asList(
                new Document(Place.NAME, getStartNameMatchPattern(name)),
                new Document(Place.TAGS, getStartNameMatchPattern(name))
        ));
        BsonDocument bsonDocument = filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
        System.out.println(bsonDocument);

        return toPlaceList(collection.find(filter).skip(page).limit(count));
    }

    @NotNull
    private Pattern getStartNameMatchPattern(String name) {
        return Pattern.compile("" + name + ".*", Pattern.CASE_INSENSITIVE);
    }

    private List<Place> toPlaceList(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(placeMapper::mapToObject).collect(toList());
    }

    public List<Place> create(Place place) {
        if (place.getId() != null) {
            throw new IllegalStateException();
        }
        final Document doc = placeMapper.mapToDocument(place);
        final String newObjectId =
                new ObjectId().toHexString();
        Document created = upsertItem(doc, newObjectId);
        return Collections.singletonList(placeMapper.mapToObject(created));
    }

    public List<Place> delete(String id) {
        List<Place> trailByCode = getById(id);
        collection.deleteOne(new Document(Trail.CODE, id));
        return trailByCode;
    }

    public List<Place> update(Place place) {
        if (place.getId() == null) {
            throw new IllegalStateException();
        }
        final Document doc = placeMapper.mapToDocument(place);
        Document created = upsertItem(doc, place.getId());
        return Collections.singletonList(placeMapper.mapToObject(created));
    }

    private Document upsertItem(final Document doc,
                                final String existingOrNewObjectId) {
        doc.append(Place.ID, existingOrNewObjectId);
        Document created = collection.findOneAndReplace(
                new Document(Place.ID, existingOrNewObjectId),
                doc,
                new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        if (created == null) {
            throw new IllegalStateException();
        }
        return created;
    }
}
