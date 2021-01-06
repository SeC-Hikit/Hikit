package org.sc.data;

import org.sc.common.rest.PoiDto;
import org.sc.data.entity.Poi;
import org.springframework.stereotype.Component;

@Component
public class PoiMapper {
    public Poi fromDto(PoiDto poi) {
        return new Poi(
                poi.getId(),
                poi.getName(),
                poi.getDescription(),
                poi.getTags(), 
                poi.getMacroType(),
                poi.getMicroType(),
                poi.getMediaIds(),
                poi.getTrailIds(),
                poi.getTrailCoordinates(),
                poi.getCreatedOn(),
                poi.getLastUpdatedOn()
        );
    }

    public PoiDto toDto(Poi poi) {
        return new PoiDto(
                poi.getId(),
                poi.getName(),
                poi.getDescription(),
                poi.getTags(),
                poi.getMacroType(),
                poi.getMicroType(),
                poi.getMediaIds(),
                poi.getTrailIds(),
                poi.getTrailCoordinates(),
                poi.getCreatedOn(),
                poi.getLastUpdatedOn()
        );
    }
}
