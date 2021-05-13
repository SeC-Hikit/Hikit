package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.MediaDto;
import org.sc.data.model.Media;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MediaMapper {
    MediaDto mediaToDto(Media trail);
}
