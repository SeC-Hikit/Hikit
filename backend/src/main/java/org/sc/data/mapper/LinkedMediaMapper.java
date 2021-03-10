package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.LinkedMediaDto;
import org.sc.data.model.LinkedMedia;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LinkedMediaMapper {
    LinkedMedia map(LinkedMediaDto linkedMediaDto);
    LinkedMediaDto map(LinkedMedia linkedMediaRequestDto);
}
