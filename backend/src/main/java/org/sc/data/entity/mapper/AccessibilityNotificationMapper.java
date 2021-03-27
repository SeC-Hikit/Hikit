package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.AccessibilityNotificationCreationDto;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.CoordinatesWithAltitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AccessibilityNotificationMapper implements Mapper<AccessibilityNotification> {

    final CoordinatesMapper coordinatesMapper;

    @Autowired
    public AccessibilityNotificationMapper(final CoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }

    @Override
    public AccessibilityNotification mapToObject(Document document) {
        final String nullableResolution = document.getString(AccessibilityNotification.RESOLUTION);
        final Date nullableResolutionDate = document.getDate(AccessibilityNotification.RESOLUTION_DATE);
        final Date reportedDate = document.getDate(AccessibilityNotification.REPORT_DATE);
        return new AccessibilityNotification(
                document.getObjectId(AccessibilityNotification.ID).toHexString(),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getString(AccessibilityNotification.TRAIL_ID),
                reportedDate,
                nullableResolutionDate == null ? reportedDate : nullableResolutionDate,
                document.getBoolean(AccessibilityNotification.IS_MINOR),
                mapToCoordinates(document),
                nullableResolution == null ? "" : nullableResolution);
    }

    @Override
    public Document mapToDocument(AccessibilityNotification accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_ID, accessibilityNotification.getTrailId())
                .append(AccessibilityNotification.ID, accessibilityNotification.get_id())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotification.getResolutionDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor())
                .append(AccessibilityNotification.COORDINATES,
                        coordinatesMapper.mapToDocument(accessibilityNotification.getCoordinates()))
                .append(AccessibilityNotification.RESOLUTION, accessibilityNotification.getResolution());
    }

    public Document mapCreationToDocument(AccessibilityNotificationCreationDto accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_ID, accessibilityNotification.getTrailId())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor())
                .append(AccessibilityNotification.COORDINATES,
                        coordinatesMapper.mapToDocument(accessibilityNotification.getCoordinates()));
    }

    private CoordinatesWithAltitude mapToCoordinates(final Document doc){
        final Document document = doc.get(AccessibilityNotification.COORDINATES, Document.class);
        return coordinatesMapper.mapToObject(document);
    }
}
