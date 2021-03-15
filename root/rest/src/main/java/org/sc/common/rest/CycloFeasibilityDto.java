package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class CycloFeasibilityDto {
    private boolean feasible;
    private int portage;
}
