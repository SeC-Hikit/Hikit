package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CycloDetails {
    private CycloClassification cycloClassification;
    private int officialEta;
    private CycloFeasability aToB;
    private CycloFeasability bToA;
    private String description;
}
