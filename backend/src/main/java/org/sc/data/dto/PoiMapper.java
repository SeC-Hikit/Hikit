package org.sc.data.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.KeyValueDto;
import org.sc.common.rest.PoiDto;
import org.sc.data.entity.KeyVal;
import org.sc.data.entity.Poi;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PoiMapper {

    @Mapping(source = "_id", target = "id")
    PoiDto poiToPoiDto(Poi poi);

    @Mapping(source = "id", target = "_id")
    Poi poiDtoToPoi(PoiDto poi);

    KeyValueDto keyValueToDto(KeyVal keyVal);

    KeyVal dtoToKeyVal(KeyValueDto keyVal);


}
