package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.entity.Trail;
import org.sc.data.entity.TrailPreview;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailPreviewMapper {
    TrailPreviewDto trailPreviewToTrailPreviewDto(TrailPreview trail);
}
