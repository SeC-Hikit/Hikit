package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailMappingDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.model.TrailMapping;
import org.sc.data.model.TrailPreview;
import org.sc.data.model.TrailRaw;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailMappingMapper {
    TrailMappingDto map(TrailMapping trail);
}
