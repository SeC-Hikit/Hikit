package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityUnresolvedDto;
import org.sc.common.rest.MaintenanceCreationDto;
import org.sc.common.rest.MaintenanceDto;
import org.sc.data.entity.AccessibilityUnresolved;
import org.sc.data.entity.Maintenance;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MaintenanceMapper {
    @Mapping(target = "id", source = "_id")
    MaintenanceDto map(Maintenance maintenance);
    @Mapping(target = "_id", source = "id")
    Maintenance map(MaintenanceDto maintenance);
    @Mapping(target = "_id", ignore = true)
    Maintenance map(MaintenanceCreationDto maintenance);

}
