package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.hikit.common.datasource.Datasource;
import org.jetbrains.annotations.NotNull;
import org.sc.common.rest.CoordinatesDto;
import org.sc.data.entity.mapper.CoordinatesMapper;
import org.sc.data.entity.mapper.PlaceMapper;
import org.sc.data.model.*;
import org.sc.util.coordinates.CoordinatesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.model.Place.*;
import static org.sc.data.repository.MongoUtils.*;

@Repository
public class PlaceDAO {
    private static final Logger LOGGER = getLogger(PlaceDAO.class);
    public static final int ONE = 1;
    public static final String DB_REALM_STRUCTURE_SELECTOR = RECORD_DETAILS + DOT + FileDetails.REALM;

    private final MongoCollection<Document> collection;
    private final PlaceMapper placeMapper;
    private final CoordinatesMapper coordinatesMapper;

    @Autowired
    public PlaceDAO(final Datasource dataSource,
                    final PlaceMapper placeMapper,
                    final CoordinatesMapper coordinatesMapper) {
        this.collection = dataSource.getDB().getCollection(COLLECTION_NAME);
        this.placeMapper = placeMapper;
        this.coordinatesMapper = coordinatesMapper;
    }

    @NotNull
    public List<Place> get(int page, int count, String realm, boolean isDynamic) {
        return toPlaceList(collection.find(
                        MongoUtils.getConditionalEqFilter(realm,
                                        DB_REALM_STRUCTURE_SELECTOR)
                                .append(IS_DYNAMIC_CROSSWAY, isDynamic))
                .skip(page).limit(count));
    }

    @NotNull
    public List<Place> getOldest(int page, int count, String realm, boolean isDynamic) {
        return toPlaceList(collection.find(
                        MongoUtils.getConditionalEqFilter(realm,
                                        DB_REALM_STRUCTURE_SELECTOR)
                                .append(IS_DYNAMIC_CROSSWAY, isDynamic))
                .sort(new Document(FileDetails.UPLOADED_ON, ASCENDING_ORDER))
                .skip(page).limit(count));
    }

    public List<Place> getById(final String id) {
        return toPlaceList(collection.find(new Document(ID, id)));
    }

    public List<Place> getLikeName(final String name, int page, int count, String realm) {
        final Bson filter =
                getLikeNameFilter(name, realm);
        return toPlaceList(collection.find(filter).skip(page).limit(count));
    }

    private Bson getLikeNameFilter(String name, String realm) {
        return MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                .append(MongoUtils.$_OR, Arrays.asList(
                        new Document(Place.NAME, getStartNameMatchPattern(name)),
                        new Document(Place.TAGS, getStartNameMatchPattern(name))));
    }

    @NotNull
    private Pattern getStartNameMatchPattern(final String name) {
        return Pattern.compile("" + name + ".*", Pattern.CASE_INSENSITIVE);
    }

    public List<Place> create(final Place place) {
        final Document doc = placeMapper.mapToDocument(place);
        final String newObjectId =
                new ObjectId().toHexString();
        Document created = upsertItem(doc, newObjectId);
        LOGGER.debug("create Place: {}, Document: {}", place, created);
        return Collections.singletonList(placeMapper.mapToObject(created));
    }

    public List<Place> delete(final String id) {
        final List<Place> places = getById(id);
        collection.deleteOne(new Document(ID, id));
        LOGGER.info("delete Places: {}, for id: {}", places, id);
        return places;
    }

    public List<Place> linkTrailToPlace(final String id,
                                        final String trailId,
                                        final CoordinatesDto trailCoordinates) {
        collection.updateOne(new Document(ID, id),
                new Document($ADD_TO_SET, new Document(CROSSING_IDS, trailId)
                        .append(POINTS + DOT + MultiPointCoords2D.COORDINATES,
                                CoordinatesUtil.INSTANCE.getLongLatFromCoordinates(trailCoordinates)))
                        .append($PUSH, new Document(COORDINATES, coordinatesMapper.mapToDocument(trailCoordinates)))
        );
        return getById(id);
    }

    public void updatePlacePoints(final String id, final List<List<Double>> trailCoordinates) {
        trailCoordinates.forEach(it ->
            collection.updateOne(new Document(ID, id),
                new Document($ADD_TO_SET,
                    new Document(POINTS + DOT + MultiPointCoords2D.COORDINATES, it)))
        );
    }

    public List<Place> removeTrailFromPlace(final String placeId,
                                            final String trailId,
                                            final Coordinates coordinates) {

        collection.updateOne(new Document(ID, placeId),
                new Document($PULL, new Document(CROSSING_IDS,
                        trailId)));

        final List<Place> afterChange = getById(placeId);

        if (afterChange.isEmpty()) {
            throw new IllegalStateException();
        }
        boolean areManyCoordinatesPresent =
                afterChange.stream().findFirst()
                        .get().getCoordinates().size() > ONE;
        if (areManyCoordinatesPresent) {
            collection.updateOne(new Document(ID, placeId),
                    new Document($PULL, new Document(COORDINATES,
                            coordinatesMapper.mapToDocument(coordinates))));
        }

        final List<Place> byId = getById(placeId);
        LOGGER.info("removeTrailFromPlace Places: {}, for placeId: {}, trailId: {}, coordinates: {}", byId, placeId, trailId, coordinates);
        return byId;
    }

    public List<Place> updateNameAndTags(final Place place) {
        if (place.getId() == null) {
            LOGGER.error("update null id for Place: {}", place);
            throw new IllegalStateException();
        }

        collection.updateOne(
                new Document(ID, place.getId()),
                new Document($_SET,
                        new Document(DESCRIPTION, place.getDescription())
                                .append(TAGS, place.getTags())
                                .append(NAME, place.getName())
                )
        );
        return getById(place.getId());
    }

    private Document upsertItem(final Document doc,
                                final String existingOrNewObjectId) {
        doc.append(ID, existingOrNewObjectId);
        Document created = collection.findOneAndReplace(
                new Document(ID, existingOrNewObjectId), doc,
                new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        if (created == null) {
            LOGGER.error("upsertItem created is null for Document: {}, existingOrNewObjectId: {}", doc, existingOrNewObjectId);
            throw new IllegalStateException();
        }
        return created;
    }

    public List<Place> addMediaToPlace(final String placeId,
                                       final LinkedMedia map) {
        collection.updateOne(new Document(ID, placeId),
                new Document($ADD_TO_SET,
                        new Document(MEDIA_IDS,
                                map.getId())));
        return getById(placeId);
    }

    public List<Place> removeMediaFromPlace(final String placeId,
                                            final String id) {
        collection.updateOne(new Document(ID, placeId),
                new Document($PULL, new Document(MEDIA_IDS,
                        id)));
        return getById(placeId);
    }

    public List<Place> getNear(double longitude, double latitude,
                               double distance, int skip, int limit) {
        return toPlaceList(collection.find(
                        new Document(POINTS,
                                getPointNearSearchQuery(longitude, latitude, distance)))
                .skip(skip)
                .limit(limit)
        );
    }

    public List<Place> getNotDynamicsNearExcludingById(double longitude,
                                                       double latitude,
                                                       double distance,
                                                       String idToExclude,
                                                       String instanceRealm) {
        return toPlaceList(collection.find(
                        new Document(POINTS,
                                getPointNearSearchQuery(longitude, latitude, distance))
                                .append(IS_DYNAMIC_CROSSWAY, false)
                                .append(DB_REALM_STRUCTURE_SELECTOR, instanceRealm)
                                .append(ID,
                                        new Document($_NOT_EQUAL, idToExclude)))
                .sort(new Document(FileDetails.UPLOADED_ON, DESCENDING_ORDER))
        );
    }

    public void addTrailsIdToPlace(@NotNull String id, @NotNull List<String> crossingTrailIds) {
        collection.updateOne(
                new Document(ID, id),
                new Document($ADD_TO_SET,
                        new Document(CROSSING_IDS, new Document($EACH, crossingTrailIds))
                )
        );
    }

    public long count() {
        return collection.countDocuments();
    }

    public long count(String realm) {
        return collection.countDocuments(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
        );
    }

    public long count(@NotNull String realm, boolean isDynamic) {
        return collection.countDocuments(
                MongoUtils.getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(IS_DYNAMIC_CROSSWAY, isDynamic)
        );
    }

    public long count(String name, String realm) {
        return collection.countDocuments(getLikeNameFilter(name, realm));
    }

    private List<Place> toPlaceList(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(placeMapper::mapToObject).collect(toList());
    }

}
