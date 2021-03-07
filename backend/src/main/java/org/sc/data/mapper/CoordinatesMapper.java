package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CoordinatesDto;
import org.sc.data.model.CoordinatesWithAltitude;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CoordinatesMapper {
    CoordinatesDto mapToCoordinatesDto(CoordinatesWithAltitude an);
    CoordinatesWithAltitude mapToCoordinatesWithAltitude(CoordinatesDto an);
}
