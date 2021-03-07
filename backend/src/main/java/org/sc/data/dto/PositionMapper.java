package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.PositionDto;
import org.sc.data.model.Position;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PositionMapper {
    Position positionDtoToPosition(PositionDto positionDto);
    PositionDto positionToPositionDto(Position positionDto);
}
