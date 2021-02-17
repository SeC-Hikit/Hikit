package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.StatsTrailMetadataDto;
import org.sc.data.entity.StatsTrailMetadata;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface StatsTrailMetadataMapper {
    StatsTrailMetadata map(StatsTrailMetadataDto statsTrailMetadataDto);
    StatsTrailMetadataDto map(StatsTrailMetadata statsTrailMetadata);
}
