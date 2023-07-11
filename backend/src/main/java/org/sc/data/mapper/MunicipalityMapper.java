package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.MunicipalityDetailsDto;
import org.sc.data.model.MunicipalityDetails;
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MunicipalityMapper {
    MunicipalityDetailsDto map(MunicipalityDetails municipalityDetails);
}
