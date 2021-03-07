package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.model.TrailPreview;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailPreviewMapper {
    TrailPreviewDto trailPreviewToTrailPreviewDto(TrailPreview trail);
}
