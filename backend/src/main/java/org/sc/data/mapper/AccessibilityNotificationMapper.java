package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.data.model.AccessibilityNotification;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccessibilityNotificationMapper {
    @Mapping(source = "_id", target = "id")
    AccessibilityNotificationDto map(AccessibilityNotification an);

    @Mapping(source = "id", target = "_id")
    AccessibilityNotification map(AccessibilityNotificationDto an);
}
