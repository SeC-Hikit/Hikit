package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailRawDto;
import org.sc.data.model.TrailRaw;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailRawMapper {
    TrailRaw map(TrailRawDto trailRawDto);
    TrailRawDto map(TrailRaw trailRaw);
}
