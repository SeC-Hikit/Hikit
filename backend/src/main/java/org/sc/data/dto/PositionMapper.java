package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.PositionDto;
import org.sc.data.entity.Position;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    Position positionDtoToPosition(PositionDto positionDto);
    PositionDto positionToPositionDto(Position positionDto);
}
