package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailDto;
import org.sc.data.entity.Trail;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailMapper {
    TrailDto trailToTrailDto(Trail trail);
    Trail trailDtoToTrail(TrailDto trail);
}
