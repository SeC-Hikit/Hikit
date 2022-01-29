package org.sc.data.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.common.rest.CoordinatesDto;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.CoordinatesMapper;
import org.sc.data.entity.mapper.PlaceMapper;
import org.sc.data.model.Coordinates;
import org.sc.data.model.LinkedMedia;
import org.sc.data.model.MultiPointCoords2D;
import org.sc.data.model.Place;
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
import static org.sc.data.model.CoordinatesWithAltitude.LAT_INDEX;
import static org.sc.data.model.CoordinatesWithAltitude.LONG_INDEX;
import static org.sc.data.repository.MongoConstants.*;

@Repository
public class PlaceDAO {
    private static final Logger LOGGER = getLogger(PlaceDAO.class);

    private final MongoCollection<Document> collection;
    private final PlaceMapper placeMapper;
    private final CoordinatesMapper coordinatesMapper;

    @Autowired
    public PlaceDAO(final DataSource dataSource,
                    final PlaceMapper placeMapper,
                    final CoordinatesMapper coordinatesMapper) {
        this.collection = dataSource.getDB().getCollection(Place.COLLECTION_NAME);
        this.placeMapper = placeMapper;
        this.coordinatesMapper = coordinatesMapper;
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
        return toPlaceList(collection.find(filter).skip(page).limit(count));
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
        collection.deleteOne(new Document(Place.ID, id));
        LOGGER.info("delete Places: {}, for id: {}", places, id);
        return places;
    }

    public List<Place> linkTrailToPlace(final String id,
                                 final String trailId, CoordinatesDto trailCoordinates) {
        collection.updateOne(new Document(Place.ID, id),
                new Document(ADD_TO_SET, new Document(Place.CROSSING,
                        trailId))
                        .append($PUSH, new Document(Place.COORDINATES, coordinatesMapper.mapToDocument(trailCoordinates)))
                        .append($PUSH, new Document(Place.POINTS + DOT + MultiPointCoords2D.COORDINATES,
                                CoordinatesUtil.INSTANCE.getLongLatFromCoordinates(trailCoordinates)))
        );
        return getById(id);
    }

    public List<Place> removeTrailFromPlace(final String id,
                                     final String trailId,
                                     final Coordinates coordinates) {

        collection.updateOne(new Document(Place.ID, id),
                new Document($PULL, new Document(Place.CROSSING,
                        trailId)));

        collection.updateOne(new Document(Place.ID, id),
                new Document($PULL, new Document(Place.COORDINATES,
                        coordinatesMapper.mapToDocument(coordinates))));

        final List<Place> byId = getById(id);
        LOGGER.info("removeTrailFromPlace Places: {}, for id: {}, trailId: {}, coordinates: {}", byId, id, trailId, coordinates);
        return byId;
    }

    public List<Place> update(final Place place) {
        if (place.getId() == null) {
            LOGGER.error("update null id for Place: {}", place);
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
                new Document(Place.ID, existingOrNewObjectId), doc,
                new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        if (created == null) {
            LOGGER.error("upsertItem created is null for Document: {}, existingOrNewObjectId: {}", doc, existingOrNewObjectId);
            throw new IllegalStateException();
        }
        return created;
    }

    public List<Place> addMediaToPlace(final String placeId,
                                       final LinkedMedia map) {
        collection.updateOne(new Document(Place.ID, placeId),
                new Document(ADD_TO_SET,
                        new Document(Place.MEDIA_IDS,
                                map.getId())));
        return getById(placeId);
    }

    public List<Place> removeMediaFromPlace(final String placeId,
                                            final String id) {
        collection.updateOne(new Document(Place.ID, placeId),
                new Document($PULL, new Document(Place.MEDIA_IDS,
                        id)));
        return getById(placeId);
    }

    public List<Place> getNear(double longitude, double latitude,
                               double distance, int skip, int limit) {
        return toPlaceList(collection.find(
                new Document(Place.POINTS,
                        getPointNearSearchQuery(longitude, latitude, distance)))
                .skip(skip)
                .limit(limit)
        );
    }

    public long count() {
        return collection.countDocuments();
    }

    private void deleteOrphanPlaceWhenNoMoreTrailsUseIt(Place place, Coordinates trailCoordinates) {
        final List<List<Double>> collect = place.getPoints().getCoordinates2D().stream().filter(p ->
                (p.get(LONG_INDEX) != trailCoordinates.getLongitude() &&
                        p.get(LAT_INDEX) != trailCoordinates.getLatitude())).collect(toList());
        LOGGER.info("deleteOrphanPlaceWhenNoMoreTrailsUseIt collect {} retrieved for Place: {}, Coordinates: {}", collect, place, trailCoordinates);
        // Mongo DB does not support empty GeoJSON multi point arrays
        if(collect.isEmpty()) {
            place.setPoints(new MultiPointCoords2D(Collections.singletonList(Arrays.asList(0.0, 0.0))));
        } else {
            place.setPoints(new MultiPointCoords2D(collect));
        }
        collection.findOneAndReplace(new Document(Place.ID, place.getId()), placeMapper.mapToDocument(place));
    }

    private List<Place> toPlaceList(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(placeMapper::mapToObject).collect(toList());
    }
}
