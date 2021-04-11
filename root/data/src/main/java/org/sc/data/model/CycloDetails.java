package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CycloDetails {

    public static final String CLASSIFICATION = "classification";
    public static final String ETA = StatsTrailMetadata.ETA;
    public static final String CYCLO_FEASIBILITY_FORWARD = "wayForward";
    public static final String CYCLO_FEASIBILITY_BACK = "wayBack";
    public static final String DESCRIPTION = "description";

    private CycloClassification cycloClassification;
    private int officialEta;
    private CycloFeasibility wayForward;
    private CycloFeasibility wayBack;
    private String description;
}
