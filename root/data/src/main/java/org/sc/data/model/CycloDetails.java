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
    private CycloFeasibility wayForward;
    private CycloFeasibility wayBack;
    private String description;
}
