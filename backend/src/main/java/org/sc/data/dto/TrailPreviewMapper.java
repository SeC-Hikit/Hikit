package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.model.TrailPreview;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailPreviewMapper {
    TrailPreviewDto trailPreviewToTrailPreviewDto(TrailPreview trail);
}
