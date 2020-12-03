package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.controller.AccessibilityNotification;
import org.sc.common.rest.controller.AccessibilityNotificationUnresolved;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationUnresolvedMapper implements Mapper<AccessibilityNotificationUnresolved> {

    @Override
    public AccessibilityNotificationUnresolved mapToObject(Document document) {
        return new AccessibilityNotificationUnresolved(
                document.getString(AccessibilityNotification.OBJECT_ID),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR));
    }

    @Override
    public Document mapToDocument(AccessibilityNotificationUnresolved accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.OBJECT_ID, accessibilityNotification.get_id())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor());
    }
}
