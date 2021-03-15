package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CycloDetailsDto;
import org.sc.data.model.CycloDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CycloDetailsMapper {
    CycloDetails map(CycloDetailsDto cycloDetailsDto);
    CycloDetailsDto map(CycloDetails cycloDetailsDto);
}
