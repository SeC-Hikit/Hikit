package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.data.model.CycloClassification;
import org.sc.data.model.TrailPreview;
import org.sc.data.model.TrailRaw;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TrailPreviewMapper {

    TrailPreviewDto map(TrailPreview trail);

    @Mapping(target = "bikeData", ignore = true, defaultValue = "false")
    @Mapping(target = "classification", ignore = true, defaultValue = "UNCLASSIFIED")
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "trailStatus", ignore = true, defaultValue = "RAW")
    @Mapping(target = "finalPos.name", ignore = true)
    @Mapping(target = "startPos.name", ignore = true)
    @Mapping(target = "startPos.placeId", ignore = true)
    @Mapping(target = "startPos.coordinates", source = "startPos")
    @Mapping(target = "finalPos.coordinates", source = "finalPos")
    @Mapping(target = "finalPos.placeId", ignore = true)
    @Mapping(target = "statsTrailMetadata", ignore = true)
    TrailPreviewDto map(TrailRaw trail);

    default TrailPreviewDto map(TrailDto trail) {
        return new TrailPreviewDto(
                trail.getId(),
                trail.getCode(),
                trail.getClassification(),
                trail.getStartLocation(),
                trail.getEndLocation(),
                trail.getLocations(),
                !List.of(CycloClassification.NO, CycloClassification.UNCLASSIFIED)
                        .contains(trail.getCycloDetails().getCycloClassification()),
                trail.getStatus(),
                trail.getStatsTrailMetadata(),
                trail.getFileDetails()
        );
    }
}
