package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CoordinatesDto;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.TrailCoordinates;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CoordinatesMapper {
    CoordinatesDto map(CoordinatesWithAltitude an);
    CoordinatesWithAltitude map(CoordinatesDto an);
    CoordinatesDto trailCoordsToDto(TrailCoordinates trailCoordinates);
    CoordinatesWithAltitude trailCoordsToAltitude(TrailCoordinates trailCoordinates);
}
