package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.common.rest.AccessibilityNotificationCreationDto;
import org.sc.data.entity.AccessibilityNotification;
import org.springframework.stereotype.Component;

@Component
public class AccessibilityNotificationCreationMapper implements Mapper<AccessibilityNotificationCreationDto> {

    @Override
    public AccessibilityNotificationCreationDto mapToObject(Document document) {
        return new AccessibilityNotificationCreationDto(
                document.getString(AccessibilityNotification.TRAIL_CODE),
                document.getString(AccessibilityNotification.DESCRIPTION),
                document.getDate(AccessibilityNotification.REPORT_DATE),
                document.getBoolean(AccessibilityNotification.IS_MINOR));
    }

    @Override
    public Document mapToDocument(AccessibilityNotificationCreationDto accessibilityNotification) {
        return new Document(AccessibilityNotification.TRAIL_CODE, accessibilityNotification.getCode())
                .append(AccessibilityNotification.DESCRIPTION, accessibilityNotification.getDescription())
                .append(AccessibilityNotification.REPORT_DATE, accessibilityNotification.getReportDate())
                .append(AccessibilityNotification.IS_MINOR, accessibilityNotification.isMinor());
    }
}
