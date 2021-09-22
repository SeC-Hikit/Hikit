package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.CycloFeasibility;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class CycloFeasibilityMapper implements Mapper<CycloFeasibility> {
    private static final Logger LOGGER = getLogger(CycloFeasibilityMapper.class);

    @Override
    public CycloFeasibility mapToObject(Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new CycloFeasibility(
                document.getBoolean(CycloFeasibility.IS_FEASIBLE),
                document.getInteger(CycloFeasibility.PORTAGE));
    }

    @Override
    public Document mapToDocument(CycloFeasibility object) {
        LOGGER.trace("mapToDocument CycloFeasibility: {} ", object);
        return new Document(CycloFeasibility.IS_FEASIBLE, object.isFeasible())
                .append(CycloFeasibility.PORTAGE, object.getPortage());
    }
}
