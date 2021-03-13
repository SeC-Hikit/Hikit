package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.PlaceDto;
import org.sc.data.model.Place;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PlaceMapper {
    Place map(PlaceDto placeDto);
    PlaceDto map(Place placeDto);
}
