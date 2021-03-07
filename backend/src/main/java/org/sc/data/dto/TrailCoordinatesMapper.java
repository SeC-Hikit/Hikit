package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.data.model.TrailCoordinates;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailCoordinatesMapper {
    TrailCoordinatesDto trailCoordinatesToTrailCoordinatesDto(TrailCoordinates value);
    TrailCoordinates trailCoordinatesDtoToTrailCoordinates(TrailCoordinatesDto value);
}
