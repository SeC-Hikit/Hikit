package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.PoiDto;
import org.sc.common.rest.TrailDto;
import org.sc.data.entity.Poi;
import org.sc.data.entity.Trail;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PoiMapper {

    @Mapping(source = "_id", target = "id")
    PoiDto poiToPoiDto(Poi poi);

    @Mapping(source = "id", target = "_id")
    Poi poiDtoToPoi(PoiDto poi);
}
