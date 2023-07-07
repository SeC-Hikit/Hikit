package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sc.data.model.PoiMacroType;

import java.util.List;

@Data
@AllArgsConstructor
public class PoiDto {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private PoiMacroType macroType;
    private List<String> microType;
    private List<LinkedMediaDto> mediaList;
    private List<String> trailIds;
    private CoordinatesDto coordinates;
    private List<String> externalResources;
    private List<KeyValueDto> keyVal;
    private RecordDetailsDto recordDetails;
    private String externalId;
    private String externalSystemName;
}