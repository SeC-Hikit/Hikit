package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CycloFeasibilityDto;
import org.sc.data.model.CycloFeasibility;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CycloFeasibilityMapper {
    CycloFeasibilityDto map(CycloFeasibility cycloDetailsDto);
    CycloFeasibility map(CycloFeasibilityDto cycloDetailsDto);
}
