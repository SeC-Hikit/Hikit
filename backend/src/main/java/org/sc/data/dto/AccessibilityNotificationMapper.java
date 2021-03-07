package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityNotificationCreationDto;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.data.model.AccessibilityNotification;
import org.sc.data.model.AccessibilityUnresolved;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccessibilityNotificationMapper {
    @Mapping(source = "_id", target = "id")
    AccessibilityNotificationDto map(AccessibilityNotification an);

    @Mapping(source = "id", target = "_id")
    AccessibilityNotification map(AccessibilityNotificationDto an);

    @Mapping(source = "_id", target = "id")
    AccessibilityUnresolvedDto map(AccessibilityUnresolved an);

    @Mapping(source = "id", target = "_id")
    AccessibilityUnresolved map(AccessibilityUnresolvedDto an);

    @Mapping(target = "_id", ignore = true)
    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "resolutionDate", ignore = true)
    AccessibilityNotification map(AccessibilityNotificationCreationDto an);

}
