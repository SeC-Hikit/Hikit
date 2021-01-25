package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.data.entity.AccessibilityNotification;
import org.sc.data.entity.AccessibilityUnresolved;

@Mapper(componentModel = "spring")
public interface AccessibilityNotificationUnrMapper {
    AccessibilityUnresolvedDto accessibilityNotificationToAccessibilityUnresolvedDto(AccessibilityUnresolved an);
}
