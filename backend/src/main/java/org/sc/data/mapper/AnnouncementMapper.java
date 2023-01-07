package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.data.model.AccessibilityNotification;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AnnouncementMapper {
    AccessibilityNotificationDto map(AccessibilityNotification an);

    @Mapping(source = "id", target = "id")
    AccessibilityNotification map(AccessibilityNotificationDto an);
}
