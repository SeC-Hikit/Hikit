package org.sc.data.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public class MongoUtils {
    public static final int ASCENDING_ORDER = 1;
    public static final int ONE = ASCENDING_ORDER;

    public static final String NO_FILTERING_TOKEN = "*";
    public static final String EXISTS_PARAM = "$exists";
    public static final String DOLLAR = "$";
    public static final String DOT = ".";
    public static final String $_OR = "$or";
    public static final String $_NOT_EQUAL = "$ne";
    public static final String $_NEAR_OPERATOR = "$near";
    public static final String $_GEOMETRY = "$geometry";
    public static final String $_SET = "$set";
    public static final String $_IN = "$in";
    public static final String NEAR_OPERATOR = "near";
    public static final String LIMIT = "$limit";
    public static final String SKIP = "$skip";
    public static final String $PULL = "$pull";
    public static final String $PUSH = "$push";
    public static final String $ADD_TO_SET = "$addToSet";
    public static final String $EACH = "$each";
    public static final String $_MAX_M_DISTANCE_FILTER = "$maxDistance";
    public static final String $_GEO_NEAR_OPERATOR = "$geoNear";
    public static final String $_GEO_WITHIN = "$geoWithin";
    public static final String $_GEO_INTERSECT = "$geoIntersects";
    public static final String $_BOX = "$box";
    public static final String DISTANCE_FIELD = "distanceField";
    public static final String KEY_FIELD = "key";
    public static final String INCLUDE_LOCS_FIELD = "includeLocs";
    public static final String MAX_DISTANCE_M = "maxDistance";
    public static final String SPHERICAL_FIELD = "spherical";
    public static final String UNIQUE_DOCS_FIELD = "uniqueDocs";

    public static final String GEO_TYPE = "type";
    public static final String GEO_POINT = "Point";
    public static final String GEO_POLYGON = "Polygon";
    public static final String GEO_LINE_STRING = "LineString";
    public static final String GEO_COORDINATES = "coordinates";

    public static final Object DESCENDING_ORDER = -1;
    public static final FindOneAndReplaceOptions UPSERT_OPTIONS = new FindOneAndReplaceOptions().upsert(true)
            .returnDocument(ReturnDocument.AFTER);

    @NotNull
    public static Document getPointNearSearchQuery(final double longitude,
                                                   final double latitude,
                                                   final double distance) {
        return new Document($_NEAR_OPERATOR,
                new Document($_GEOMETRY,
                        new Document(GEO_TYPE, GEO_POINT).append(
                                GEO_COORDINATES, Arrays.asList(longitude, latitude)
                        )).append($_MAX_M_DISTANCE_FILTER, distance)
        );
    }

    public static Document getConditionalEqFilter(final String realm, final String dbRealmStructureSelector) {
        return realm.equals(NO_FILTERING_TOKEN) ? getNoFilter(dbRealmStructureSelector) :
                getRealmFilter(realm, dbRealmStructureSelector);
    }

    public static Pattern getAnyMatchingPattern(final String name) {
        return Pattern.compile(".*" + name + ".*", Pattern.CASE_INSENSITIVE);
    }

    public static Pattern getAnyStartingPattern(final String name) {
        return Pattern.compile(name + ".*", Pattern.CASE_INSENSITIVE);
    }

    private static Document getRealmFilter(final String realm, final String dbRealmStructureSelector) {
        return new Document(dbRealmStructureSelector, realm);
    }

    private static Document getNoFilter(final String dbRealmStructureSelector) {
        return new Document(dbRealmStructureSelector, new Document($_NOT_EQUAL, ""));
    }

    @NotNull
    public static Pattern getStartNameMatchPattern(final String name) {
        return Pattern.compile("^" + name + ".*", Pattern.CASE_INSENSITIVE);
    }

    public static void logBson(Document document) {
        BsonDocument bsonDocument = document.toBsonDocument(BsonDocument.class,
                MongoClient.getDefaultCodecRegistry());
        System.out.println(bsonDocument);
    }

}
