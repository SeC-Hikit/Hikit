package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailDto;
import org.sc.data.model.Trail;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailMapper {
    TrailDto map(Trail trail);
    @Mapping(target = "geoLineString", ignore = true)
    Trail map(TrailDto trail);
}
