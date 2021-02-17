package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.data.entity.TrailCoordinates;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailCoordinatesMapper {
    TrailCoordinatesDto trailCoordinatesToTrailCoordinatesDto(TrailCoordinates value);
    TrailCoordinates trailCoordinatesDtoToTrailCoordinates(TrailCoordinatesDto value);
}
