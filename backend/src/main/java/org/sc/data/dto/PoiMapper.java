package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.sc.common.rest.PoiDto;
import org.sc.common.rest.TrailDto;
import org.sc.data.entity.Poi;
import org.sc.data.entity.Trail;

@Mapper(componentModel = "spring")
public interface PoiMapper {
    PoiDto poiToPoiDto(Poi poi);
    Poi poiDtoToPoi(PoiDto poi);
}
