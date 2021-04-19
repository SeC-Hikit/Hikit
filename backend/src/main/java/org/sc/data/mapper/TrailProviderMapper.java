package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailProviderDto;
import org.sc.data.model.TrailProvider;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailProviderMapper {
    TrailProviderDto map(TrailProvider trailProvider);
    TrailProvider map(TrailProviderDto trailProviderDto);
}
