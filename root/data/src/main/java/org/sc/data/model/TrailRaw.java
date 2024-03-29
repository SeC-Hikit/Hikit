package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TrailRaw {

    public static final String COLLECTION_NAME = "core.Raw";

    public static String ID = Trail.ID;
    public static String NAME = Trail.NAME;
    public static String DESCRIPTION = Trail.DESCRIPTION;
    public static String START_POS = Trail.START_POS;
    public static String FINAL_POS = Trail.FINAL_POS;
    public static String COORDINATES = Trail.COORDINATES;
    public static String FILE_DETAILS = Trail.RECORD_DETAILS;

    private String id;
    private String name;
    private String description;
    private TrailCoordinates startPos;
    private TrailCoordinates finalPos;
    private List<TrailCoordinates> coordinates;
    private FileDetails fileDetails;
}
