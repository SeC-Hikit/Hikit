package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.data.entity.AccessibilityNotification;
import org.sc.data.entity.AccessibilityUnresolved;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccessibilityNotificationUnrMapper {
    @Mapping(source = "_id", target = "id")
    AccessibilityUnresolvedDto accessibilityNotificationToAccessibilityUnresolvedDto(AccessibilityUnresolved an);
}
