package org.sc.data.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;
import org.sc.data.entity.Poi;
import org.sc.data.entity.mapper.PoiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;
import static org.sc.data.repository.MongoConstants.*;

import static java.util.stream.Collectors.toList;

@Component
public class PoiDAO {

    private final MongoCollection<Document> collection;
    private final PoiMapper poiMapper;

    @Autowired
    public PoiDAO(final DataSource dataSource,
                  final PoiMapper poiMapper) {
        this.collection = dataSource.getDB().getCollection(Poi.COLLECTION_NAME);
        this.poiMapper = poiMapper;
    }

    public List<Poi> get(final int page,
                         final int count) {
        return toPoisList(collection.find().skip(page).limit(count));
    }

    public List<Poi> getById(final String id) {
        return toPoisList(collection.find(new Document(Poi.OBJECT_ID, id)));
    }

    public List<Poi> getByCode(final String code,
                               final int page,
                               final int count) {
        return toPoisList(collection.find(new Document(Poi.TRAIL_CODES, code)).skip(page).limit(count));
    }

    public List<Poi> getByMacro(final String macroType,
                                final int page,
                                final int count) {
        return toPoisList(collection.find(new Document(Poi.MACROTYPE, macroType)).skip(page).limit(count));
    }

    public List<Poi> getByName(final String name,
                               int page,
                               int count) {
        return toPoisList(collection.find(new Document(Poi.NAME, name)).skip(page).limit(count));
    }

    public List<Poi> getByTags(final String tag,
                               final int page,
                               final int count) {
        return toPoisList(collection.find(new Document(Poi.TAGS, tag)).skip(page).limit(count));
    }

    @NotNull
    public List<Poi> getByPosition(double longitude, double latitude, double meters, int page, int count) {
        final AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(new Document($_GEO_NEAR_OPERATOR,
                        new Document(NEAR_OPERATOR, new Document("type", "Point")
                                .append("coordinates", Arrays.asList(longitude, latitude)))
                                .append(DISTANCE_FIELD, "distanceToIt")
                                .append(KEY_FIELD, "coordinates.coordinates")
                                .append(INCLUDE_LOCS_FIELD, "closestLocation")
                                .append(MAX_DISTANCE_M, meters)
                                .append(SPHERICAL_FIELD, "true")
                                .append(UNIQUE_DOCS_FIELD, "true")),
                new Document(SKIP, page),
                new Document(LIMIT, count)
        ));
        return toPoisList(aggregate);
    }

    public List<Poi> upsert(final Poi poiRequest) {
        // TODO: shall upsert and update the date
        final Document trail = poiMapper.mapToDocument(poiRequest);
        UpdateResult updateResult = collection.replaceOne(new Document(Poi.OBJECT_ID, poiRequest.get_id()),
                trail, new ReplaceOptions().upsert(true));
        if(updateResult.getModifiedCount() > 0){
            return Collections.singletonList(new Poi(updateResult.getUpsertedId().asString().toString(),
                    poiRequest.getName(),
                    poiRequest.getDescription(), poiRequest.getTags(), poiRequest.getMacroType(), poiRequest.getMicroType(),
                    poiRequest.getMediaIds(), poiRequest.getTrailIds(), poiRequest.getTrailCoordinates(), poiRequest.getCreatedOn(),
                     poiRequest.getLastUpdatedOn(), poiRequest.getExternalResources()));
        }
        throw new IllegalArgumentException();
    }

    public List<Poi> delete(final String id) {
        final List<Poi> byId = getById(id);
        collection.deleteOne(new Document(Poi.OBJECT_ID, id));
        return byId;
    }

    @NotNull
    private List<Poi> toPoisList(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(poiMapper::mapToObject).collect(toList());
    }
}
