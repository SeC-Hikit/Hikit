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

    PoiDto poiToPoiDto(Poi poi);

    Poi map(PoiDto poi);

    KeyValueDto keyValueToDto(KeyVal keyVal);

    KeyVal dtoToKeyVal(KeyValueDto keyVal);


}
