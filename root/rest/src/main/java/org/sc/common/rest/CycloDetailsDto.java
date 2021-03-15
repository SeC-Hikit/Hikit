package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sc.data.model.CycloClassification;

@Data
@AllArgsConstructor
public class CycloDetailsDto {
    private CycloClassification cycloClassification;
    private int officialEta;
    private CycloFeasibilityDto wayForward;
    private CycloFeasibilityDto wayBack;
    private String description;
}
