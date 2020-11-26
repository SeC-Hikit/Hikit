package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.controller.AccessibilityNotification;
import org.sc.common.rest.controller.AccessibilityNotificationCreation;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationCreationMapper implements Mapper<AccessibilityNotificationCreation> {

    @Override
    public AccessibilityNotificationCreation mapToObject(Document document) {
        return new AccessibilityNotificationCreation(
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR));
    }

    @Override
    public Document mapToDocument(AccessibilityNotificationCreation accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor());
    }
}
