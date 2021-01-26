package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.data.entity.AccessibilityNotification;

// TODO: ensure mapping works
@Mapper(componentModel = "spring")
public interface AccessibilityNotificationMapper {
    @Mapping(source = "_id", target = "id")
    AccessibilityNotificationDto accessibilityNotificationToAccessibilityNotificationDto(AccessibilityNotification an);
}
