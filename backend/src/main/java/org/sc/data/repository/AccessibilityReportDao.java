package org.sc.data.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.configuration.DataSource;
import org.sc.data.entity.mapper.AccessibilityReportMapper;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.AccessibilityReport;
import org.sc.data.model.RecordDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.sc.data.repository.MongoConstants.$NOT_EQUAL;

@Repository
public class AccessibilityReportDao {

    private final MongoCollection<Document> collection;

    private final AccessibilityReportMapper mapper;

    @Autowired
    public AccessibilityReportDao(final DataSource dataSource,
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
        throw new IllegalStateException();
    }

    public List<AccessibilityReport> delete(final String id) {
        final List<AccessibilityReport> byId = getById(id);
        collection.deleteOne(new Document(AccessibilityReport.ID, id));
        return byId;
    }

    public List<AccessibilityReport> deleteByTrailId(final String trailId) {
        final List<AccessibilityReport> byTrailId = getByTrailId(trailId);
        collection.deleteMany(new Document(AccessibilityReport.TRAIL_ID, trailId));
        return byTrailId;
    }

    private List<AccessibilityReport> getByTrailId(final String trailId) {
        return toNotificationList(collection.find(
                new Document(AccessibilityReport.TRAIL_ID, new ObjectId(trailId))));
    }

    public List<AccessibilityReport> update(final AccessibilityReport accReport) {
        List<AccessibilityReport> found = getById(accReport.getId());
        if (found.isEmpty()) return Collections.emptyList();
        collection.updateOne(new Document(AccessibilityReport.ID, accReport.getId()),
                new Document(AccessibilityReport.REPORT_DATE, accReport.getReportDate())
                        .append(AccessibilityReport.TELEPHONE, accReport.getTelephone())
                        .append(AccessibilityReport.ISSUE_ID, accReport.getIssueId())
                        .append(AccessibilityReport.DESCRIPTION, accReport.getDescription())
                        .append(AccessibilityReport.EMAIL, accReport.getEmail())
        );
        return found;
    }

    public List<AccessibilityReport> getUnapgradedByRealm(final String realm, final int skip, final int limit) {
        return toNotificationList(collection.find(
                        new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm)
                                .append(AccessibilityReport.ISSUE_ID, ""))
                .sort(new Document(AccessibilityReport.REPORT_DATE, MongoConstants.ASCENDING_ORDER))
                .skip(skip)
                .limit(limit)
        );
    }

    public List<AccessibilityReport> getUpgradedByRealm(final String realm,
                                                        final int skip,
                                                        final int limit) {
        return toNotificationList(collection.find(
                        new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm)
                                .append(AccessibilityReport.ISSUE_ID, new Document($NOT_EQUAL, "")))
                .sort(new Document(AccessibilityReport.REPORT_DATE, MongoConstants.ASCENDING_ORDER))
                .skip(skip)
                .limit(limit)
        );
    }

    public long count() {
        return collection.countDocuments();
    }

    public long countAccessibility(final String realm) {
        return collection.countDocuments(
                new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm));
    }

    public long countUnapgraded(final String realm) {
        return collection.countDocuments(
                new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm)
                        .append(AccessibilityReport.ISSUE_ID, ""));
    }

    public long countUpgraded(final String realm) {
        return collection.countDocuments(
                new Document(AccessibilityReport.RECORD_DETAILS + "." + RecordDetails.REALM, realm)
                        .append(AccessibilityReport.ISSUE_ID, new Document($NOT_EQUAL, "")));
    }

    public List<AccessibilityReport> validate(final String validationId) {
        if (!getByValidationId(validationId).isEmpty()) {
            collection.updateOne(new Document(AccessibilityReport.VALIDATION_ID, validationId),
                    new Document(AccessibilityReport.IS_VALID, true));
        }
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
