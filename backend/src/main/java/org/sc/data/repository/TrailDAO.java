package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.*;
import org.sc.data.geo.CoordinatesRectangle;
import org.sc.data.model.*;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.repository.MongoConstants.*;

@Repository
public class TrailDAO {
    private static final Logger LOGGER = getLogger(TrailDAO.class);

    public static final String PLACE_ID_IN_LOCATIONS = Trail.LOCATIONS + DOT + PlaceRef.PLACE_ID;
    public static final String NO_FILTERING = "*";
    public static final String REALM_STRUCT = Trail.RECORD_DETAILS + DOT + FileDetails.REALM;
    public static final String POSITIONAL_EVERY_OPERATOR = ".$[].";
    public static final String POSITIONAL_OPERATOR = ".$";
    public static final String START_POS_COORDINATES = Trail.START_POS + "." + PlaceRef.COORDINATES + "." + PlaceRef.COORDINATES;
    public static final String FINAL_POS_COORDINATES = Trail.FINAL_POS + "." + PlaceRef.COORDINATES + "." + PlaceRef.COORDINATES;


    private final MongoCollection<Document> collection;

    private final Mapper<Trail> trailMapper;
    private final SelectiveArgumentMapper<Trail> trailLevelMapper;
    private final Mapper<TrailPreview> trailPreviewMapper;
    private final Mapper<TrailMapping> trailMappingMapper;
    private final LinkedMediaMapper linkedMediaMapper;
    private final PlaceRefMapper placeRefMapper;
    private final CycloMapper cycloMapper;


    @Autowired
    public TrailDAO(final DataSource dataSource,
                    final TrailMapper trailMapper,
                    final SelectiveArgumentMapper<Trail> trailLevelMapper,
                    final Mapper<TrailMapping> trailMappingMapper,
                    final LinkedMediaMapper linkedMediaMapper,
                    final TrailPreviewMapper trailPreviewMapper,
                    final PlaceRefMapper placeRefMapper,
                    final CycloMapper cycloMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);
        this.trailMapper = trailMapper;
        this.trailLevelMapper = trailLevelMapper;
        this.trailMappingMapper = trailMappingMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.trailPreviewMapper = trailPreviewMapper;
        this.placeRefMapper = placeRefMapper;
        this.cycloMapper = cycloMapper;
    }

    public List<Trail> getTrails(int skip, int limit,
                                 final TrailSimplifierLevel trailSimplifierLevel,
                                 final String realm) {
        final Document realmFilter = getFilter(realm);
        return toTrailsList(collection.find(realmFilter).skip(skip).limit(limit),
                trailSimplifierLevel);
    }

    public List<TrailMapping> getTrailsMappings(int skip, int limit, final String realm) {
        final Document realmFilter = getFilter(realm);
        return toTrailsMappingList(collection.find(realmFilter)
                .projection(new Document(Trail.ID, ONE)
                        .append(Trail.CODE, ONE)
                        .append(Trail.NAME, ONE))
                .skip(skip).limit(limit));
    }

    public List<TrailPreview> getTrailPreviewById(final String id) {
        return toTrailsPreviewList(collection.find(new Document(Trail.ID, id)));
    }

    public List<Trail> getTrailById(final String id,
                                    final TrailSimplifierLevel trailSimplifierLevel) {
        return toTrailsList(collection.find(new Document(Trail.ID, id)), trailSimplifierLevel);
    }

    public List<Trail> getTrailByPlaceId(final String id,
                                         final int page,
                                         final int limit,
                                         final TrailSimplifierLevel trailSimplifierLevel) {
        return toTrailsList(collection.find(new Document(PLACE_ID_IN_LOCATIONS, id)).skip(page).limit(limit),
                trailSimplifierLevel);
    }

    public List<Trail> delete(final String id) {
        List<Trail> trailByCode = getTrailById(id, TrailSimplifierLevel.MEDIUM);
        collection.deleteOne(new Document(Trail.ID, id));
        LOGGER.info("delete Trails: {}, for id: {}", trailByCode, id);
        return trailByCode;
    }

    public List<Trail> upsert(final Trail trailRequest) {
        final String existingOrNewObjectId = trailRequest.getId() == null ?
                new ObjectId().toHexString() : trailRequest.getId();
        final Document trailDocument = trailMapper.mapToDocument(trailRequest)
                .append(Trail.ID, existingOrNewObjectId);
        final Document updateResult = collection.findOneAndReplace(
                new Document(Trail.ID, existingOrNewObjectId),
                trailDocument, UPSERT_OPTIONS);
        if (updateResult == null) {
            LOGGER.error("upsert updateResult is null for Trail: {}, existingOrNewObjectId: {}", trailRequest, existingOrNewObjectId);
            throw new IllegalStateException();
        }
        return Collections.singletonList(trailMapper.mapToObject(updateResult));
    }

    public List<TrailPreview> getTrailPreviews(final int skip, final int limit, final String realm) {

        final Document filter = realm.equals(NO_FILTERING_TOKEN) ? getNoFilter() : getRealmFilter(realm);
        final Bson project = getTrailPreviewProjection();

        final Bson aLimit = Aggregates.limit(limit);
        final Bson aSkip = Aggregates.skip(skip);

        return toTrailsPreviewList(collection.aggregate(Arrays.asList(match(filter), project, aLimit, aSkip)));
    }

    public List<TrailPreview> findPreviewsByCode(final String code, final int skip,
                                                 final int limit, final String realm) {
        final Document codeFilter = getLikeEndFilter(Trail.CODE, code);
        final Document realmFilter = realm.equals(NO_FILTERING_TOKEN) ? getNoFilter() : getRealmFilter(realm);
        final Bson project = getTrailPreviewProjection();
        final Bson aLimit = Aggregates.limit(limit);
        final Bson aSkip = Aggregates.skip(skip);
        return toTrailsPreviewList(collection.aggregate(
                Arrays.asList(match(codeFilter),
                        match(realmFilter), project, aLimit, aSkip)));
    }

    public List<TrailPreview> trailPreviewById(final String id) {
        final Bson project = getTrailPreviewProjection();
        final Bson equalId = match(eq(Trail.ID, id));
        return toTrailsPreviewList(collection.aggregate(Arrays.asList(equalId, project)));
    }

    public void unlinkMediaByAllTrails(final String mediaId) {
        // E.g: db.core.test.update({"b.mediaId": 1}, { $pull : { "b.$.mediaId": 1}}, {multi: true})
        collection.updateMany(new Document(),
                new Document($PULL, new Document((Trail.MEDIA),
                        new Document(LinkedMedia.ID, mediaId))));
    }

    public List<Trail> linkMedia(final String id,
                                 final LinkedMedia linkMedia) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document($ADD_TO_SET, new Document(Trail.MEDIA,
                        linkedMediaMapper.mapToDocument(linkMedia))));
        return getTrailById(id, TrailSimplifierLevel.LOW);
    }

    public List<Trail> unlinkMedia(final String id,
                                   final String mediaId) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document($PULL, new Document(Trail.MEDIA,
                        new Document(LinkedMedia.ID, mediaId))));
        return getTrailById(id, TrailSimplifierLevel.LOW);
    }

    public List<Trail> findTrailWithinGeoSquare(
            final CoordinatesRectangle geoSquare,
            final int skip,
            final int limit,
            final TrailSimplifierLevel level,
            final boolean isDraftTrailVisible) {
        final List<Double> resolvedTopLeftVertex = resolveVertex(geoSquare.getBottomLeft(), geoSquare.getTopRight());
        final List<Double> resolvedBottomRightVertex = resolveVertex(geoSquare.getTopRight(), geoSquare.getBottomLeft());
        final FindIterable<Document> foundTrails = foundTrailsWithinSquare(geoSquare, skip, limit, resolvedTopLeftVertex, resolvedBottomRightVertex, isDraftTrailVisible);
        LOGGER.trace("findTrailWithinGeoSquare geoSquare: {}, skip: {}, limit: {}, level: {}, resolvedTopLeftVertex: {}, resolvedBottomRightVertex: {}",
                geoSquare, skip, limit, level, resolvedTopLeftVertex, resolvedBottomRightVertex);
        return toTrailsList(foundTrails, level);
    }

    public List<TrailMapping> findTrailMappingWithinGeoSquare(
            final CoordinatesRectangle geoSquare,
            final int skip, final int limit) {
        final List<Double> resolvedTopLeftVertex = resolveVertex(geoSquare.getBottomLeft(), geoSquare.getTopRight());
        final List<Double> resolvedBottomRightVertex = resolveVertex(geoSquare.getTopRight(), geoSquare.getBottomLeft());
        final FindIterable<Document> foundTrails = foundTrailsWithinSquare(geoSquare, skip, limit,
                resolvedTopLeftVertex, resolvedBottomRightVertex, true)
                .projection(new Document(Trail.ID, ONE).append(Trail.CODE, ONE).append(Trail.NAME, ONE));
        return toTrailMappingList(foundTrails);
    }

    public List<Trail> findTrailPerfectlyContainedInGeoSquare(
            CoordinatesRectangle outerGeoSquare,
            final int skip, final int limit, final TrailSimplifierLevel level) {
        FindIterable<Document> foundTrails = collection.find(new Document(Trail.GEO_LINE,
                new Document($_GEO_WITHIN, new Document(
                        $_BOX,
                        Arrays.asList(outerGeoSquare.getBottomLeft().getAsList(),
                                outerGeoSquare.getTopRight().getAsList())
                )))).skip(skip).limit(limit);
        return toTrailsList(foundTrails, level);
    }

    public void linkGivenTrailToPlace(String id, PlaceRef placeRef) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document($ADD_TO_SET, new Document(Trail.LOCATIONS,
                        placeRefMapper.mapToDocument(placeRef))));
    }

    public List<Trail> unLinkPlace(String id, PlaceRef placeRef) {
        collection.updateOne(new Document(Trail.ID, id),
                new Document($PULL, new Document(Trail.LOCATIONS,
                        new Document(PlaceRef.PLACE_ID, placeRef.getPlaceId()))));
        return getTrailById(id, TrailSimplifierLevel.LOW);
    }

    public void unlinkPlaceFromAllTrails(String placeId) {
        Document update = new Document($PULL, new Document(Trail.LOCATIONS,
                new Document(PlaceRef.PLACE_ID, placeId)));
        collection.updateMany(new Document(),
                update);
    }

    public void propagatePlaceRemovalFromRefs(String placeId, String trailId) {
        LOGGER.trace("Propagating trail locations removal");
        collection.updateMany(new Document(Trail.LOCATIONS + "." + PlaceRef.PLACE_ID, placeId),
                new Document($PULL, new Document(
                        Trail.LOCATIONS + POSITIONAL_EVERY_OPERATOR + PlaceRef.ENCOUNTERED_TRAIL_IDS, trailId
                ))
        );
    }

    public void linkAllExistingTrailConnectionWithNewTrailId(String placeId, String trailId) {
        LOGGER.trace(String.format("Connecting trail with trailID='%s', with placeId='%s'", trailId, placeId));
        collection.updateMany(new Document(PLACE_ID_IN_LOCATIONS, placeId),
                new Document($ADD_TO_SET,
                        new Document(Trail.LOCATIONS + POSITIONAL_OPERATOR + "." + PlaceRef.ENCOUNTERED_TRAIL_IDS, trailId)));
    }

    public void updatePlacesRefsByTrailId(final String trailId,
                                          final List<PlaceRef> reorderedPlaces) {
        collection.updateOne(new Document(Trail.ID, trailId),
                new Document($_SET, new Document(Trail.LOCATIONS,
                        reorderedPlaces.stream()
                                .map(placeRefMapper::mapToDocument)
                                .collect(Collectors.toList()))));
    }

    public long countTrail() {
        return collection.countDocuments();
    }

    public long countTrailByRealm(final String realm) {
        return collection.countDocuments(getRealmFilter(realm));
    }

    private List<TrailPreview> toTrailsPreviewList(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailPreviewMapper::mapToObject).collect(toList());
    }

    private List<TrailMapping> toTrailMappingList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailMappingMapper::mapToObject).collect(toList());
    }

    private List<TrailMapping> toTrailsMappingList(FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailMappingMapper::mapToObject).collect(toList());
    }

    private List<Trail> toTrailsList(final Iterable<Document> documents,
                                     final TrailSimplifierLevel trailSimplifierLevel) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(t -> trailLevelMapper.mapToObject(t, trailSimplifierLevel)).collect(toList());
    }

    private Document getRealmFilter(final String realm) {
        return new Document(REALM_STRUCT, realm);
    }

    private Document getNoFilter() {
        return new Document(REALM_STRUCT, new Document($NOT_EQUAL, ""));
    }

    private Document getLikeEndFilter(String fieldName, String valueToConcatenate) {
        return new Document(fieldName, getStartNameMatchPattern(valueToConcatenate));
    }

    @NotNull
    private FindIterable<Document> foundTrailsWithinSquare(CoordinatesRectangle geoSquare, int skip, int limit, List<Double> resolvedTopLeftVertex, List<Double> resolvedBottomRightVertex, boolean isDraftTrailVisible) {
        List<String> filter = Collections.emptyList();
        if (isDraftTrailVisible) {
            //array (public draft)
            filter=Arrays.asList("public","draft");
        }
        else {
            //array (public)
            filter=Arrays.asList("public");
        }

            return collection.find(
                new Document(Trail.STATUS, filter).append(Trail.GEO_LINE,
                        new Document($_GEO_INTERSECT,
                                new Document($_GEOMETRY, new Document(GEO_TYPE, GEO_POLYGON)
                                        .append(GEO_COORDINATES,
                                                Collections.singletonList(
                                                        Arrays.asList(
                                                                geoSquare.getBottomLeft().getAsList(),
                                                                resolvedTopLeftVertex,
                                                                geoSquare.getTopRight().getAsList(),
                                                                resolvedBottomRightVertex,
                                                                geoSquare.getBottomLeft().getAsList()
                                                        )
                                                )
                                        ))))).skip(skip).limit(limit);
    }

    private Bson getTrailPreviewProjection() {
        return project(fields(
                include(Trail.CLASSIFICATION),
                include(Trail.CYCLO),
                include(Trail.STATUS),
                include(Trail.LOCATIONS),
                include(Trail.RECORD_DETAILS),
                include(Trail.LAST_UPDATE_DATE),
                include(Trail.CODE),
                computed(Trail.START_POS,
                        new Document("$arrayElemAt",
                                Arrays.asList(DOLLAR + Trail.LOCATIONS, 0))),
                computed(Trail.FINAL_POS,
                        new Document("$arrayElemAt",
                                Arrays.asList(DOLLAR + Trail.LOCATIONS, -1)))
        ));
    }

    @NotNull
    public List<Trail> update(@NotNull Trail trail) {
        collection.updateOne(
                new Document(Trail.ID, trail.getId()),
                new Document($_SET,
                        new Document(Trail.DESCRIPTION, trail.getDescription()).append(Trail.CODE, trail.getCode())
                                .append(Trail.CLASSIFICATION, trail.getClassification().toString())
                                .append(Trail.COUNTRY, trail.getCountry())
                                .append(Trail.OFFICIAL_ETA, trail.getOfficialEta())
                                .append(Trail.LAST_UPDATE_DATE, trail.getLastUpdate())
                                .append(Trail.MEDIA, trail.getMediaList())
                                .append(Trail.STATUS, trail.getStatus().toString())
                                .append(Trail.CYCLO, cycloMapper.mapToDocument(trail.getCycloDetails()))
                                .append(Trail.NAME, trail.getName())
                                .append(Trail.TERRITORIAL_CARED_BY, trail.getTerritorialDivision())
                ));
        return getTrailById(trail.getId(), TrailSimplifierLevel.LOW);
    }

    public List<TrailMapping> getByStartEndPoint(final double startLatitude, final double startLongitude,
                                                 final double endLatitude, final double endLongitude) {
        final FindIterable<Document> documents = collection.find(
                new Document(START_POS_COORDINATES, Arrays.asList(startLongitude, startLatitude))
                        .append(FINAL_POS_COORDINATES, Arrays.asList(endLongitude, endLatitude)));

        return toTrailsMappingList(documents);
    }


    private List<Double> resolveVertex(Coordinates2D bottomLeft, Coordinates2D topRight) {
        return Arrays.asList(bottomLeft.getLongitude(),
                topRight.getLatitude());
    }

    @NotNull
    private Document getFilter(String realm) {
        return realm.equals(NO_FILTERING) ? new Document() :
                new Document(Trail.RECORD_DETAILS + DOT + FileDetails.REALM, realm);
    }

    public long countTotalByCode(final String code) {
        return collection.countDocuments(getLikeEndFilter(Trail.CODE, code));
    }


}