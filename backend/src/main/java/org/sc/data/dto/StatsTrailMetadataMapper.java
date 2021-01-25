package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.StatsTrailMetadataDto;
import org.sc.data.entity.StatsTrailMetadata;

@Mapper(componentModel = "spring")
public interface StatsTrailMetadataMapper {
    StatsTrailMetadata map(StatsTrailMetadataDto value);
    StatsTrailMetadataDto map(StatsTrailMetadata statsTrailMetadata);
}
