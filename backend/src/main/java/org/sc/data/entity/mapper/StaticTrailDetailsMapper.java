package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.StaticTrailDetails;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.model.StaticTrailDetails.*;

@Component
public class StaticTrailDetailsMapper implements Mapper<StaticTrailDetails> {
    private static final Logger LOGGER = getLogger(StaticTrailDetailsMapper.class);

    @Override
    public StaticTrailDetails mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new StaticTrailDetails(
                document.getString(PATH_GPX),
                document.getString(PATH_KML),
                document.getString(PATH_PDF));
    }

    @Override
    public Document mapToDocument(final StaticTrailDetails object) {
        LOGGER.trace("mapToDocument FileDetails: {} ", object);
        return new Document()
                .append(PATH_GPX, object.getPathGpx())
                .append(PATH_KML, object.getPathKml())
                .append(PATH_PDF, object.getPathPdf());
    }
}
