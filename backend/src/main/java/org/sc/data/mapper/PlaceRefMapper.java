package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.PlaceRefDto;
import org.sc.data.model.Place;
import org.sc.data.model.PlaceRef;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PlaceRefMapper {
    PlaceRef map(PlaceRefDto placeDto);
    PlaceRefDto map(PlaceRef placeDto);
}
