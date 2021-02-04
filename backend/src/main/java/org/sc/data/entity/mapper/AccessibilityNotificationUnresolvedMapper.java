package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.data.entity.AccessibilityNotification;
import org.sc.data.entity.AccessibilityUnresolved;
import org.sc.data.entity.CoordinatesWithAltitude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationUnresolvedMapper implements Mapper<AccessibilityUnresolved> {

    final CoordinatesMapper coordinatesMapper;

    @Autowired
    public AccessibilityNotificationUnresolvedMapper(final CoordinatesMapper coordinatesMapper) {
        this.coordinatesMapper = coordinatesMapper;
    }
    @Override
    public AccessibilityUnresolved mapToObject(Document document) {
        return new AccessibilityUnresolved(
                document.getObjectId(AccessibilityNotification.OBJECT_ID).toHexString(),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR),
                mapToCoordinates(document));
    }

    @Override
    public Document mapToDocument(AccessibilityUnresolved accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.OBJECT_ID, accessibilityNotification.get_id())
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
