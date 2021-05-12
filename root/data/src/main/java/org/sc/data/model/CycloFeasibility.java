package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CycloFeasibility {

    public static final String IS_FEASIBLE = "feasible";
    public static final String PORTAGE = "portage";

    private boolean feasible;
    private int portage;
}
