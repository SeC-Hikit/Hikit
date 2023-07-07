package org.sc.common.rest;

import lombok.Data;
import org.sc.data.model.*;

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
    private PlaceRefDto startLocation;
    private PlaceRefDto endLocation;
    private List<PlaceRefDto> locations;
    private TrailClassification classification;
    private String country;
    private StatsTrailMetadataDto statsTrailMetadata;
    private List<TrailCoordinatesDto> coordinates;
    private List<LinkedMediaDto> mediaList;
    private Date lastUpdate;
    private String territorialDivision;
    private String maintainingSection;
    private TrailStatus status;
    private FileDetailsDto fileDetails;
    private StaticTrailDetailsDto staticTrailDetails;
    private CycloDetailsDto cycloDetails;
    private List<MunicipalityDetailsDto> municipalities;
}
