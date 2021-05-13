package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.CycloFeasibility;
import org.springframework.stereotype.Component;

@Component
public class CycloFeasibilityMapper implements Mapper<CycloFeasibility> {

    @Override
    public CycloFeasibility mapToObject(Document document) {
        return new CycloFeasibility(
                document.getBoolean(CycloFeasibility.IS_FEASIBLE),
                document.getInteger(CycloFeasibility.PORTAGE));
    }

    @Override
    public Document mapToDocument(CycloFeasibility object) {
        return new Document(CycloFeasibility.IS_FEASIBLE, object.isFeasible())
                .append(CycloFeasibility.PORTAGE, object.getPortage());
    }
}
