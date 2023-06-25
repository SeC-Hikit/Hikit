package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sc.data.model.PlaceRef;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class TrailPreviewDto {
    private String id;
    private String code;
    private TrailClassification classification;
    private PlaceRefDto startPos;
    private PlaceRefDto finalPos;
    private List<PlaceRefDto> locations;
    private boolean bikeData;
    private TrailStatus trailStatus;
    private StatsTrailMetadataDto statsTrailMetadata;
    private FileDetailsDto fileDetails;
}