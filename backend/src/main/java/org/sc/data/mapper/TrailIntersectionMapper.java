package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailIntersectionDto;
import org.sc.data.model.TrailIntersection;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailIntersectionMapper {
    TrailIntersectionDto map(TrailIntersection trailPointIntersection);
}
