package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CycloFeasability {
    private boolean isFeasible;
    private int portage;
}
