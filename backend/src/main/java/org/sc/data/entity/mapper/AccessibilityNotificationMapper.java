package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.CoordinatesWithAltitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AccessibilityNotificationMapper implements Mapper<AccessibilityNotification> {

    final CoordinatesMapper coordinatesMapper;
    private final RecordDetailsMapper recordDetailsMapper;

    @Autowired
    public AccessibilityNotificationMapper(final CoordinatesMapper coordinatesMapper,
                                           final RecordDetailsMapper recordDetailsMapper) {
        this.coordinatesMapper = coordinatesMapper;
        this.recordDetailsMapper = recordDetailsMapper;
    }

    @Override
    public AccessibilityNotification mapToObject(final Document document) {
        final String nullableResolution = document.getString(AccessibilityNotification.RESOLUTION);
        final Date nullableResolutionDate = document.getDate(AccessibilityNotification.RESOLUTION_DATE);
        final Date reportedDate = document.getDate(AccessibilityNotification.REPORT_DATE);
        return new AccessibilityNotification(
                document.getString(AccessibilityNotification.ID),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getString(AccessibilityNotification.TRAIL_ID),
                reportedDate,
                nullableResolutionDate == null ? reportedDate : nullableResolutionDate,
                document.getBoolean(AccessibilityNotification.IS_MINOR),
                mapToCoordinates(document),
                nullableResolution == null ? "" : nullableResolution,
                recordDetailsMapper.mapToObject(document.get(AccessibilityNotification.RECORD_DETAILS, Document.class))
        );
    }

    @Override
    public Document mapToDocument(final AccessibilityNotification accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_ID, accessibilityNotification.getTrailId())
                .append(AccessibilityNotification.ID, accessibilityNotification.getId())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotification.getResolutionDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor())
                .append(AccessibilityNotification.COORDINATES,
                        coordinatesMapper.mapToDocument(accessibilityNotification.getCoordinates()))
                .append(AccessibilityNotification.RESOLUTION, accessibilityNotification.getResolution())
                .append(AccessibilityNotification.RECORD_DETAILS,
                        recordDetailsMapper.mapToDocument(accessibilityNotification.getRecordDetails()));
    }

    private CoordinatesWithAltitude mapToCoordinates(final Document doc) {
        final Document document = doc.get(AccessibilityNotification.COORDINATES, Document.class);
        return coordinatesMapper.mapToObject(document);
    }
}
