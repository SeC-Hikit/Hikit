package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CoordinatesDto;
import org.sc.data.model.CoordinatesWithAltitude;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CoordinatesMapper {
    CoordinatesDto mapToCoordinatesDto(CoordinatesWithAltitude an);
    CoordinatesWithAltitude mapToCoordinatesWithAltitude(CoordinatesDto an);
}
