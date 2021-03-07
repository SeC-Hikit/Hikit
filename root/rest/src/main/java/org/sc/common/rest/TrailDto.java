package org.sc.common.rest;

import lombok.Data;
import org.sc.data.model.TrailClassification;

import java.util.Date;
import java.util.List;

@Data
public class TrailDto {
    private String id;
    private String code;
    private String name;
    private String description;
    private boolean variant;
    private int officialEta;
    private PositionDto startPos;
    private PositionDto finalPos;
    private List<PositionDto> locations;
    private TrailClassification classification;
    private String country;
    private StatsTrailMetadataDto statsTrailMetadata;
    private List<TrailCoordinatesDto> coordinates;
    private List<LinkedMediaDto> mediaList;
    private Date lastUpdate;
    private Date createdOn;
    private String territorialDivision;
    private String maintainingSection;
}
