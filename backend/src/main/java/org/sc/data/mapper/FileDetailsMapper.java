package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.FileDetailsDto;
import org.sc.data.model.FileDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FileDetailsMapper {
    FileDetails map(FileDetailsDto trailRawDto);
    FileDetailsDto map(FileDetails trailRaw);
}
