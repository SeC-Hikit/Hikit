package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.data.entity.AccessibilityNotification;

@Mapper(componentModel = "spring")
public interface AccessibilityNotificationMapper {
    AccessibilityNotificationDto accessibilityNotificationToAccessibilityNotificationDto(AccessibilityNotification an);
}
