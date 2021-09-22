package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.TrailDatasetVersion;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class TrailDatasetMapper implements Mapper<TrailDatasetVersion> {
    private static final Logger LOGGER = getLogger(TrailDatasetMapper.class);

    public static final String VERSION_FIELD = "version";
    public static final String LAST_UPDATE_FIELD = "lastUpdate";

    @Override
    public TrailDatasetVersion mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new TrailDatasetVersion(document.getLong(VERSION_FIELD), document.getDate(LAST_UPDATE_FIELD));
    }

    @Override
    public Document mapToDocument(final TrailDatasetVersion object) {
        LOGGER.trace("mapToDocument TrailDatasetVersion: {} ", object);
        return new Document(VERSION_FIELD, object.getVersion()).append(LAST_UPDATE_FIELD, object.getLastUpdate());
    }
}
