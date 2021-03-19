package org.sc.common.rest;

import lombok.Data;
import org.sc.data.model.TrailClassification;
import org.sc.data.model.TrailStatus;

import java.util.Date;
import java.util.Objects;

@Data
public class TrailPreviewDto {
    private String code;
    private TrailClassification classification;
    private PlaceRefDto startPos;
    private PlaceRefDto finalPos;
    private boolean bikeData;
    private TrailStatus trailStatus;
    private FileDetailsDto fileDetails;
}