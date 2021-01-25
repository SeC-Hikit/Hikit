package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.data.entity.AccessibilityNotification;
import org.sc.data.entity.AccessibilityUnresolved;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationUnresolvedMapper implements Mapper<AccessibilityUnresolved> {

    @Override
    public AccessibilityUnresolved mapToObject(Document document) {
        return new AccessibilityUnresolved(
                document.getString(AccessibilityNotification.OBJECT_ID),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR));
    }

    @Override
    public Document mapToDocument(AccessibilityUnresolved accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.OBJECT_ID, accessibilityNotification.get_id())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor());
    }
}
