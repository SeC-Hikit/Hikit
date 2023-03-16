package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.StaticTrailDetailsDto;
import org.sc.data.model.StaticTrailDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StaticTrailDetailsMapper {
    StaticTrailDetails map(StaticTrailDetailsDto staticFileDto);
    StaticTrailDetailsDto map(StaticTrailDetails recordDetailsDto);
}
