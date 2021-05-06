package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.RecordDetailsDto;
import org.sc.data.model.RecordDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RecordDetailsMapper {
    RecordDetails map(RecordDetailsDto recordDetailsDto);
    RecordDetailsDto map(RecordDetails recordDetailsDto);

}
