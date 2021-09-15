package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.AccessibilityReport;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityReportMapper implements Mapper<AccessibilityReport> {

    private final CoordinatesMapper trailCoordinatesMapper;
    private final RecordDetailsMapper recordDetailsMapper;

    public AccessibilityReportMapper(final CoordinatesMapper trailCoordinatesMapper,
                                     final RecordDetailsMapper recordDetailsMapper) {
        this.trailCoordinatesMapper = trailCoordinatesMapper;
        this.recordDetailsMapper = recordDetailsMapper;
    }

    @Override
    public AccessibilityReport mapToObject(Document document) {
        return new AccessibilityReport(document.getString(AccessibilityReport.ID),
                document.getString(AccessibilityReport.DESCRIPTION),
                document.getString(AccessibilityReport.TRAIL_ID),
                document.getString(AccessibilityReport.EMAIL),
                document.getString(AccessibilityReport.TELEPHONE),
                document.getDate(AccessibilityReport.REPORT_DATE),
                document.getString(AccessibilityReport.ISSUE_ID),
                document.getString(AccessibilityReport.VALIDATION_ID),
                document.getBoolean(AccessibilityReport.IS_VALID),
                trailCoordinatesMapper.mapToObject(document.get(AccessibilityReport.COORDINATES, Document.class)),
                recordDetailsMapper.mapToObject(document.get(AccessibilityReport.RECORD_DETAILS, Document.class)));
    }

    @Override
    public Document mapToDocument(AccessibilityReport object) {
        return new Document(AccessibilityReport.ID, object.getId())
                .append(AccessibilityReport.DESCRIPTION, object.getDescription())
                .append(AccessibilityReport.TRAIL_ID, object.getTrailId())
                .append(AccessibilityReport.EMAIL, object.getEmail())
                .append(AccessibilityReport.TELEPHONE, object.getTelephone())
                .append(AccessibilityReport.REPORT_DATE, object.getReportDate())
                .append(AccessibilityReport.ISSUE_ID, object.getIssueId())
                .append(AccessibilityReport.VALIDATION_ID, object.getValidationId())
                .append(AccessibilityReport.COORDINATES, trailCoordinatesMapper.mapToDocument(object.getCoordinates()))
                .append(AccessibilityReport.RECORD_DETAILS, recordDetailsMapper.mapToDocument(object.getRecordDetails()));
    }
}
