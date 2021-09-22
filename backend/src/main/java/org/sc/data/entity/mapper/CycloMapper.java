package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class CycloMapper implements Mapper<CycloDetails> {
    private static final Logger LOGGER = getLogger(CycloMapper.class);

    private final CycloFeasibilityMapper cycloDetailsMapper;

    @Autowired
    public CycloMapper(CycloFeasibilityMapper cycloDetailsMapper) {
        this.cycloDetailsMapper = cycloDetailsMapper;
    }

    @Override
    public CycloDetails mapToObject(Document doc) {
        LOGGER.trace("mapToObject Document: {} ", doc);
        return new CycloDetails(getClassification(doc), doc.getInteger(CycloDetails.ETA),
                cycloDetailsMapper.mapToObject(doc.get(CycloDetails.CYCLO_FEASIBILITY_FORWARD, Document.class)),
                cycloDetailsMapper.mapToObject(doc.get(CycloDetails.CYCLO_FEASIBILITY_BACK, Document.class)),
                doc.getString(CycloDetails.DESCRIPTION));
    }

    @Override
    public Document mapToDocument(CycloDetails object) {
        LOGGER.trace("mapToDocument CycloDetails: {} ", object);
        return new Document(CycloDetails.CLASSIFICATION, object.getCycloClassification().getClassification())
                .append(CycloDetails.ETA, object.getOfficialEta())
                .append(CycloDetails.CYCLO_FEASIBILITY_FORWARD, object.getWayForward())
                .append(CycloDetails.CYCLO_FEASIBILITY_BACK, object.getWayBack())
                .append(CycloDetails.DESCRIPTION, object.getDescription());
    }


    private CycloClassification getClassification(Document doc) {
        final String classification = doc.getString(CycloDetails.CLASSIFICATION);
        return CycloClassification.valueOf(classification);
    }
}
