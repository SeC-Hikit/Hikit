package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailDto;
import org.sc.data.model.Trail;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailMapper {
    TrailDto trailToTrailDto(Trail trail);
    Trail trailDtoToTrail(TrailDto trail);
}
