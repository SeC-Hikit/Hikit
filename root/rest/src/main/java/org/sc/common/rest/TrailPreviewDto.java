package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class TrailPreviewDto {
    private String id;
    private String code;
    private TrailClassification classification;
    private PlaceRefDto startPos;
    private PlaceRefDto finalPos;
    private boolean bikeData;
    private TrailStatus trailStatus;
    private FileDetailsDto fileDetails;
}