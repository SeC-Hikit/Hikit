package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.MaintenanceDto;
import org.sc.data.model.Maintenance;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MaintenanceMapper {
    MaintenanceDto map(Maintenance maintenance);
    Maintenance map(MaintenanceDto maintenance);
}
