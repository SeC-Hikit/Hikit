package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.data.entity.TrailCoordinates;

// TODO: ensure mapping works
@Mapper(componentModel = "spring")
public interface TrailCoordinatesMapper {
    TrailCoordinatesDto trailCoordinatesToTrailCoordinatesDto(TrailCoordinates value);
    TrailCoordinates trailCoordinatesDtoToTrailCoordinates(TrailCoordinatesDto value);
}
