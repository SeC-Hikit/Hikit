package org.sc.data.dto;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.StatsTrailMetadataDto;
import org.sc.data.entity.StatsTrailMetadata;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface StatsTrailMetadataMapper {
    StatsTrailMetadata map(StatsTrailMetadataDto statsTrailMetadataDto);
    StatsTrailMetadataDto map(StatsTrailMetadata statsTrailMetadata);
}
