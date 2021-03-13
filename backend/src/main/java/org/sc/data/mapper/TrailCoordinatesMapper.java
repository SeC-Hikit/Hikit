package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.data.model.TrailCoordinates;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailCoordinatesMapper {
    TrailCoordinatesDto map(TrailCoordinates value);
    TrailCoordinates map(TrailCoordinatesDto value);
}
