package org.sc.data.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.hikit.common.datasource.Datasource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sc.data.entity.mapper.*;
import org.sc.data.geo.CoordinatesRectangle;
import org.sc.data.model.*;
import org.sc.data.repository.helper.StatusFilterHelper;
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
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.*;
import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.repository.MongoUtils.*;

@Repository
public class TrailDAO {
    private static final Logger LOGGER = getLogger(TrailDAO.class);

    public static final String PLACE_ID_IN_LOCATIONS = Trail.LOCATIONS + DOT + PlaceRef.PLACE_ID;
    public static final String POSITIONAL_EVERY_OPERATOR = ".$[].";
    public static final String POSITIONAL_OPERATOR = ".$";

    public static final String PLACE_ID_IN_LOCATIONS_POSITIONAL = Trail.LOCATIONS + POSITIONAL_OPERATOR + DOT + PlaceRef.PLACE_ID;
    public static final String PLACE_NAME_IN_LOCATIONS_POSITIONAL = Trail.LOCATIONS + POSITIONAL_OPERATOR + DOT + PlaceRef.NAME;
    public static final String START_POS_COORDINATES = Trail.START_POS + "." + PlaceRef.COORDINATES + "." + PlaceRef.COORDINATES;
    public static final String FINAL_POS_COORDINATES = Trail.FINAL_POS + "." + PlaceRef.COORDINATES + "." + PlaceRef.COORDINATES;
    public static final String DB_REALM_STRUCTURE_SELECTOR = Trail.RECORD_DETAILS + "." + FileDetails.REALM;


    private final MongoCollection<Document> collection;

    private final Mapper<Trail> trailMapper;
    private final StatusFilterHelper statusFilterHelper;
    private final SelectiveArgumentMapper<Trail> trailLevelMapper;
    private final Mapper<String> trailCodeMapper;
    private final Mapper<TrailPreview> trailPreviewMapper;
    private final Mapper<TrailMapping> trailMappingMapper;
    private final LinkedMediaMapper linkedMediaMapper;
    private final PlaceRefMapper placeRefMapper;
    private final CycloMapper cycloMapper;


    @Autowired
    public TrailDAO(final Datasource dataSource,
                    final TrailMapper trailMapper,
                    final StatusFilterHelper statusFilterHelper,
                    final SelectiveArgumentMapper<Trail> trailLevelMapper,
                    final Mapper<TrailMapping> trailMappingMapper,
                    final LinkedMediaMapper linkedMediaMapper,
                    final TrailPreviewMapper trailPreviewMapper,
                    final PlaceRefMapper placeRefMapper,
                    final CycloMapper cycloMapper,
                    final TrailCodeMapper trailCodeMapper) {
        this.collection = dataSource.getDB().getCollection(Trail.COLLECTION_NAME);

        this.trailMapper = trailMapper;
        this.statusFilterHelper = statusFilterHelper;
        this.trailLevelMapper = trailLevelMapper;
        this.trailMappingMapper = trailMappingMapper;
        this.linkedMediaMapper = linkedMediaMapper;
        this.trailPreviewMapper = trailPreviewMapper;
        this.placeRefMapper = placeRefMapper;
        this.cycloMapper = cycloMapper;
        this.trailCodeMapper = trailCodeMapper;
    }

    public List<Trail> getTrails(int skip, int limit,
                                 final TrailSimplifierLevel trailSimplifierLevel,
                                 final String realm,
                                 final boolean isDraftTrailVisible) {
        final Document realmFilter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        return toTrailsList(collection.find(
                                realmFilter.append(Trail.STATUS,
                                        statusFilterHelper.getInFilterBson(isDraftTrailVisible)))
                        .skip(skip).limit(limit),
                trailSimplifierLevel);
    }

    // TODO: add tests on this
    public List<TrailMapping> getTrailsMappings(int skip, int limit,
                                                final String realm,
                                                boolean isDraftTrailVisible) {
        final Document realmFilter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        return toTrailsMappingList(collection.find(realmFilter
                        .append(Trail.STATUS, statusFilterHelper.getInFilterBson(isDraftTrailVisible)))
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

    public List<String> getCodesById(final List<String> id) {
        return toTrailCodeList(collection.find(new Document(Trail.ID, new Document($_IN, id))));
    }

    public List<Trail> getTrailByPlaceId(final String id,
                                         final int page,
                                         final int limit,
                                         final TrailSimplifierLevel trailSimplifierLevel) {
        return toTrailsList(collection.find(new Document(PLACE_ID_IN_LOCATIONS, id))
                        .skip(page)
                        .limit(limit),
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
            LOGGER.error("upsert updateResult is null for Trail: {}, existingOrNewObjectId: {}",
                    trailRequest, existingOrNewObjectId);
            throw new IllegalStateException();
        }
        return Collections.singletonList(trailMapper.mapToObject(updateResult));
    }

    public List<TrailPreview> getTrailPreviews(final int skip, final int limit,
                                               final String realm, boolean isDraftTrailVisible) {
        final Bson statusFilter = getBsonAggregateStatusInFilter(isDraftTrailVisible);
        final Document filter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        final Bson project = getTrailPreviewProjection();
        final Bson aLimit = Aggregates.limit(limit);
        final Bson aSkip = Aggregates.skip(skip);
        final Bson aOrder = Aggregates.sort(new Document(Trail.CODE, 1));
        return toTrailsPreviewList(
                collection.aggregate(
                        Arrays.asList(
                                match(filter),
                                match(statusFilter),
                                project, aLimit, aSkip, aOrder)));
    }

    public List<TrailPreview> findPreviewsByCode(final String code, final int skip,
                                                 final int limit, final String realm,
                                                 final boolean isDraftTrailVisible) {
        final Bson statusFilter = getBsonAggregateStatusInFilter(isDraftTrailVisible);
        final Document codeFilter = new Document(Trail.CODE, getStartNameMatchPattern(code));
        final Bson aOrder = Aggregates.sort(new Document(Trail.CODE, 1));
        final Document realmFilter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        final Bson project = getTrailPreviewProjection();
        final Bson aLimit = Aggregates.limit(limit);
        final Bson aSkip = Aggregates.skip(skip);
        return toTrailsPreviewList(collection.aggregate(
                Arrays.asList(match(codeFilter),
                        match(statusFilter),
                        match(realmFilter),
                        project, aLimit, aSkip, aOrder)));
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

    public List<TrailPreview> searchByLocationOrTrailName(
            String name,
            String realm,
            boolean isDraftTrailVisible,
            int skip, int limit) {
        final Document realmFilter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        final Bson statusFilter = getBsonAggregateStatusInFilter(isDraftTrailVisible);
        final Bson aLimit = Aggregates.limit(limit);
        final Bson aSkip = Aggregates.skip(skip);
        final Bson aOrder = Aggregates.sort(new Document(Trail.CODE, 1));
        final Document filter = new Document($_OR, Arrays.asList(
                new Document("locations.name", getAnyMatchingPattern(name)),
                new Document("name", getAnyMatchingPattern(name))
        ));
        final AggregateIterable<Document> foundTrails =
                collection.aggregate(
                        Arrays.asList(
                                match(filter),
                                match(statusFilter),
                                match(realmFilter),
                                aLimit, aSkip, aOrder));
        return toTrailsPreviewList(foundTrails);
    }


    public long countTrail() {
        return collection.countDocuments();
    }

    public long countTrailByRealm(final String realm, boolean isDraftTrailVisible) {
        return collection.countDocuments(
                getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Trail.STATUS, statusFilterHelper.getInFilterBson(isDraftTrailVisible)));
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

    private List<String> toTrailCodeList(final Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false)
                .map(trailCodeMapper::mapToObject).collect(toList());
    }

    @NotNull
    private FindIterable<Document> foundTrailsWithinSquare(final CoordinatesRectangle geoSquare,
                                                           final int skip,
                                                           final int limit,
                                                           final List<Double> resolvedTopLeftVertex,
                                                           final List<Double> resolvedBottomRightVertex,
                                                           final boolean isDraftTrailVisible) {
        final List<String> inStatusFilter = statusFilterHelper.getInFilter(isDraftTrailVisible);
        return collection.find(
                new Document(Trail.STATUS,
                        new Document($_IN, inStatusFilter))
                        .append(Trail.GEO_LINE,
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

    private Bson getBsonAggregateStatusInFilter(boolean isDraftTrailVisible) {
        final List<String> inStatusFilter =
                statusFilterHelper.getInFilter(isDraftTrailVisible);
        return in(Trail.STATUS, inStatusFilter);
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

    @NotNull
    public List<Trail> updateTrailNamePlaceReference(final String trailId,
                                                     final String placeId,
                                                     final String placeName) {
        collection.updateOne(
                new Document(Trail.ID, trailId)
                        .append(Trail.START_POS + DOT + PlaceRef.PLACE_ID, placeId),
                new Document($_SET,
                        new Document(Trail.START_POS + DOT + PlaceRef.NAME, placeName)));
        collection.updateOne(
                new Document(Trail.ID, trailId)
                        .append(Trail.FINAL_POS + DOT + PlaceRef.PLACE_ID, placeId),
                new Document($_SET,
                        new Document(Trail.FINAL_POS + DOT + PlaceRef.NAME, placeName)));
        collection.updateOne(
                new Document(Trail.ID, trailId)
                        .append(Trail.LOCATIONS + DOT + PlaceRef.PLACE_ID, placeId),
                new Document($_SET,
                        new Document(Trail.LOCATIONS + DOT + DOLLAR + DOT + PlaceRef.NAME, placeName)));

        return getTrailById(trailId, TrailSimplifierLevel.LOW);
    }

    public void updateAllPlaceReferencesWithNewPlaceId(@NotNull String oldId,
                                                       @NotNull String id,
                                                       @NotNull String name
                                                       ) {
        collection.updateMany(
                new Document(PLACE_ID_IN_LOCATIONS, oldId),
                new Document($_SET, new Document(PLACE_ID_IN_LOCATIONS_POSITIONAL, id))
                        .append($_SET, new Document(PLACE_NAME_IN_LOCATIONS_POSITIONAL, name)));
    }


    public List<TrailMapping> getByStartEndPoint(final double startLatitude, final double startLongitude,
                                                 final double endLatitude, final double endLongitude) {
        final FindIterable<Document> documents = collection.find(
                new Document(START_POS_COORDINATES, Arrays.asList(startLongitude, startLatitude))
                        .append(FINAL_POS_COORDINATES, Arrays.asList(endLongitude, endLatitude)));

        return toTrailsMappingList(documents);
    }

    public long countTotalByCode(final String realm, final String code, boolean isDraftTrailVisible) {
        return collection.countDocuments(
                getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR)
                        .append(Trail.CODE, getStartNameMatchPattern(code))
                        .append(Trail.STATUS, statusFilterHelper.getInFilterBson(isDraftTrailVisible)));
    }

    private List<Double> resolveVertex(Coordinates2D bottomLeft, Coordinates2D topRight) {
        return Arrays.asList(bottomLeft.getLongitude(),
                topRight.getLatitude());
    }

    public long countFindingByNameOrLocationName(@Nullable String name, @NotNull String realm,
                                                 boolean draftTrailVisible) {
        final Document realmFilter = getConditionalEqFilter(realm, DB_REALM_STRUCTURE_SELECTOR);
        final Bson statusFilter = getBsonAggregateStatusInFilter(draftTrailVisible);
        return collection.countDocuments(
                new Document($_OR, Arrays.asList(
                        new Document("locations.name", getAnyMatchingPattern(name)),
                        new Document("name", getAnyMatchingPattern(name)),
                        realmFilter, statusFilter
                )));
    }


}