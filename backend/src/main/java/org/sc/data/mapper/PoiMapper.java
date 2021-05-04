package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.KeyValueDto;
import org.sc.common.rest.PoiDto;
import org.sc.data.model.KeyVal;
import org.sc.data.model.Poi;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PoiMapper {

    @Mapping(source = "_id", target = "id")
    PoiDto poiToPoiDto(Poi poi);

    @Mapping(source = "id", target = "id")
    Poi map(PoiDto poi);

    KeyValueDto keyValueToDto(KeyVal keyVal);

    KeyVal dtoToKeyVal(KeyValueDto keyVal);


}
