package org.sc.data.dto;

import org.sc.common.rest.PoiDto;
import org.sc.data.entity.Poi;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PoiDtoMapper implements Mapper<Poi, PoiDto>{

    @Override
    public Poi toEntity(final PoiDto poi) {
        return new Poi(
                Objects.requireNonNull(poi.getId()),
                poi.getName(),
                poi.getDescription(),
                poi.getTags(), 
                poi.getMacroType(),
                poi.getMicroType(),
                poi.getMediaIds(),
                poi.getTrailIds(),
                poi.getTrailCoordinates(),
                poi.getCreatedOn(),
                poi.getLastUpdatedOn(),
                poi.getExternalResources()
        );
    }

    @Override
    public PoiDto toDto(final Poi poi) {
        return new PoiDto(
                poi.getId(),
                poi.getName(),
                poi.getDescription(),
                poi.getTags(),
                poi.getMacroType(),
                poi.getMicroType(),
                poi.getMediaIds(),
                poi.getTrailCodes(),
                poi.getTrailCoordinates(),
                poi.getCreatedOn(),
                poi.getLastUpdatedOn(),
                poi.getExternalResources()
        );
    }
}
