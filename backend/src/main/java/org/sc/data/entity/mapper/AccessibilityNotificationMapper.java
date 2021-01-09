package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.AccessibilityNotification;
import org.sc.data.entity.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationMapper implements Mapper<AccessibilityNotification> {

    @Override
    public AccessibilityNotification mapToObject(Document document) {
        return new AccessibilityNotification(
                document.getString(AccessibilityNotification.OBJECT_ID),
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getDate(AccessibilityNotification.RESOLUTION_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR),
                document.getString(AccessibilityNotification.RESOLUTION));
    }

    @Override
    public Document mapToDocument(AccessibilityNotification accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.RESOLUTION_DATE, accessibilityNotification.getResolutionDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor())
                .append(AccessibilityNotification.RESOLUTION, accessibilityNotification.getResolution());
    }
}
