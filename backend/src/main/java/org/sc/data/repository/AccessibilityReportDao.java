package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.hikit.common.datasource.Datasource;
import org.sc.data.entity.mapper.AccessibilityReportMapper;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.AccessibilityReport;
import org.sc.data.model.RecordDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.repository.MongoUtils.$_NOT_EQUAL;
import static org.sc.data.repository.MongoUtils.DOT;

@Repository
public class AccessibilityReportDao {
    private static final Logger LOGGER = getLogger(AccessibilityReportDao.class);

    public static final String COLLECTION_REALM_STRUCT = AccessibilityReport.RECORD_DETAILS + DOT + RecordDetails.REALM;

    private final MongoCollection<Document> collection;

    private final AccessibilityReportMapper mapper;

    @Autowired
    public AccessibilityReportDao(final Datasource dataSource,
                                  AccessibilityReportMapper accessibilityReportMapper) {
        this.collection = dataSource.getDB().getCollection(AccessibilityReport.COLLECTION_NAME);
        this.mapper = accessibilityReportMapper;
    }

    public List<AccessibilityReport> getById(String id) {
        return toNotificationList(collection.find(
                new Document(AccessibilityReport.ID, id)));
    }

    public List<AccessibilityReport> getByValidationId(String validationId) {
        return toNotificationList(collection.find(
                new Document(AccessibilityReport.VALIDATION_ID, validationId)));
    }

    public List<AccessibilityReport> getByTrailId(String trailId, int skip, int limit) {
        return toNotificationList(collection.find(
                        new Document(AccessibilityReport.TRAIL_ID, trailId))
                .skip(skip)
                .limit(limit)
        );
    }

    public List<AccessibilityReport> upsert(final AccessibilityReport accessibilityReport, final String validationId) {
        final Document accessibilityNotificationDocument = mapper.mapToDocument(accessibilityReport);
        accessibilityNotificationDocument.append(AccessibilityReport.VALIDATION_ID, validationId);
        accessibilityNotificationDocument.append(AccessibilityReport.IS_VALID, false);
        final String existingOrNewObjectId = accessibilityReport.getId() == null ?
                new ObjectId().toHexString() : accessibilityReport.getId();
        accessibilityNotificationDocument.append(AccessibilityNotification.ID, existingOrNewObjectId);
        final Document addedResult = collection.findOneAndReplace(
                new Document(AccessibilityNotification.ID, existingOrNewObjectId),
                accessibilityNotificationDocument, new FindOneAndReplaceOptions().upsert(true)
                        .returnDocument(ReturnDocument.AFTER));
        if (addedResult != null) {
            return Collections.singletonList(mapper.mapToObject(addedResult));
        }
        LOGGER.error("upsert addedResult is null for AccessibilityReport: {}, validationId: {}", accessibilityReport, validationId);
        throw new IllegalStateException();
    }

    public List<AccessibilityReport> delete(final String id) {
        final List<AccessibilityReport> byId = getById(id);
        collection.deleteOne(new Document(AccessibilityReport.ID, id));
        LOGGER.info("deleted AccessibilityReports: {}, for id: {}", byId, id);
        return byId;
    }

    public List<AccessibilityReport> deleteByTrailId(final String trailId) {
        final List<AccessibilityReport> byTrailId = getByTrailId(trailId);
        collection.deleteMany(new Document(AccessibilityReport.TRAIL_ID, trailId));
        LOGGER.info("deleted AccessibilityReports: {}, for id: {}", byTrailId, trailId);
        return byTrailId;
    }

    private List<AccessibilityReport> getByTrailId(final String trailId) {
        return toNotificationList(collection.find(
                new Document(AccessibilityReport.TRAIL_ID, new ObjectId(trailId))));
    }

    public List<AccessibilityReport> update(final AccessibilityReport accReport) {
        String id = accReport.getId();
        List<AccessibilityReport> found = getById(id);
        LOGGER.info("update AccessibilityReports: {}, for AccessibilityReport: {}", found, accReport);
        if (found.isEmpty()) return Collections.emptyList();
        collection.updateOne(new Document(AccessibilityReport.ID, id),
                new Document(MongoUtils.$_SET,
                        new Document(AccessibilityReport.REPORT_DATE, accReport.getReportDate())
                                .append(AccessibilityReport.TELEPHONE, accReport.getTelephone())
                                .append(AccessibilityReport.ISSUE_ID, accReport.getIssueId())
                                .append(AccessibilityReport.DESCRIPTION, accReport.getDescription())
                                .append(AccessibilityReport.EMAIL, accReport.getEmail()))
        );
        return getById(id);
    }

    public List<AccessibilityReport> getUnapgradedByRealm(final String realm, final int skip, final int limit) {
        return toNotificationList(collection.find(
                        new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm)
                                .append(AccessibilityReport.ISSUE_ID, "")
                                .append(AccessibilityReport.IS_VALID, true))
                .sort(new Document(AccessibilityReport.REPORT_DATE, MongoUtils.ASCENDING_ORDER))
                .skip(skip)
                .limit(limit)
        );
    }

    public List<AccessibilityReport> getUpgradedByRealm(final String realm,
                                                        final int skip,
                                                        final int limit) {
        return toNotificationList(collection.find(
                        new Document(COLLECTION_REALM_STRUCT, realm)
                                .append(AccessibilityReport.ISSUE_ID, new Document($_NOT_EQUAL, "")))
                .sort(new Document(AccessibilityReport.REPORT_DATE, MongoUtils.ASCENDING_ORDER))
                .skip(skip)
                .limit(limit)
        );
    }

    public List<String> getActivationIdById(String id) {
        return StreamSupport.stream(collection.find(new Document(AccessibilityReport.ID, id))
                        .projection(new Document(AccessibilityReport.VALIDATION_ID, MongoUtils.ONE))
                        .map(entry -> entry.getString(AccessibilityReport.VALIDATION_ID)).spliterator(), false)
                .collect(Collectors.toList());
    }

    public long count() {
        return collection.countDocuments();
    }

    public long countAccessibility(final String realm) {
        return collection.countDocuments(
                new Document(COLLECTION_REALM_STRUCT, realm));
    }

    public long countUnapgraded(final String realm) {
        return collection.countDocuments(
                new Document(COLLECTION_REALM_STRUCT, realm)
                        .append(AccessibilityReport.ISSUE_ID, ""));
    }

    public long countUpgraded(final String realm) {
        return collection.countDocuments(
                new Document(COLLECTION_REALM_STRUCT, realm)
                        .append(AccessibilityReport.ISSUE_ID, new Document($_NOT_EQUAL, "")));
    }

    public List<AccessibilityReport> validate(final String validationId) {
        if (!getByValidationId(validationId).isEmpty()) {
            collection.updateOne(new Document(AccessibilityReport.VALIDATION_ID, validationId),
                    new Document(MongoUtils.$_SET, new Document(AccessibilityReport.IS_VALID, true)));
            return getByValidationId(validationId);
        }
        LOGGER.info("validate empty getByValidationId for id: {}", validationId);
        return Collections.emptyList();
    }

    public long countByTrailId(final String id) {
        return collection.countDocuments(
                new Document(AccessibilityReport.TRAIL_ID, id));
    }

    private List<AccessibilityReport> toNotificationList(final FindIterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).map(mapper::mapToObject).collect(toList());
    }
}
