package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.common.rest.TrailDto;
import org.sc.data.entity.Trail;
import org.sc.data.entity.TrailCoordinates;

@Mapper(componentModel = "spring")
public interface TrailCoordinatesMapper {
    TrailCoordinatesDto trailCoordinatesToTrailCoordinatesDto(TrailCoordinates trail);
    TrailCoordinates trailCoordinatesDtoToTrailCoordinates(TrailCoordinatesDto trail);
}
