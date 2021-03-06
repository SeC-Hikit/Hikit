package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.PositionDto;
import org.sc.data.entity.Position;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PositionMapper {
    Position positionDtoToPosition(PositionDto positionDto);
    PositionDto positionToPositionDto(Position positionDto);
}
