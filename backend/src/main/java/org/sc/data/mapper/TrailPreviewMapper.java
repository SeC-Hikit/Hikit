package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.model.TrailPreview;
import org.sc.data.model.TrailRaw;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailPreviewMapper {

    TrailPreviewDto map(TrailPreview trail);

    @Mapping(target = "bikeData", ignore = true, defaultValue = "false")
    @Mapping(target = "classification", ignore = true, defaultValue = "UNCLASSIFIED")
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "trailStatus", ignore = true, defaultValue = "RAW")
    @Mapping(target = "finalPos.name", ignore = true)
    @Mapping(target = "startPos.name", ignore = true)
    @Mapping(target = "startPos.placeId", ignore = true)
    @Mapping(target = "startPos.coordinates", source = "startPos")
    @Mapping(target = "finalPos.coordinates", source = "finalPos")
    @Mapping(target = "finalPos.placeId", ignore = true)
    TrailPreviewDto map(TrailRaw trail);
}
