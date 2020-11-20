package org.sc.data;

import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class TrailDatasetMapper implements Mapper<TrailDatasetVersion> {

    public static final String VERSION_FIELD = "version";
    public static final String LAST_UPDATE_FIELD = "lastUpdate";

    @Override
    public TrailDatasetVersion mapToObject(final Document document) {
        return new TrailDatasetVersion(document.getLong(VERSION_FIELD), document.getDate(LAST_UPDATE_FIELD));
    }

    @Override
    public Document mapToDocument(final TrailDatasetVersion object) {
        return new Document(VERSION_FIELD, object.getVersion()).append(LAST_UPDATE_FIELD, object.getLastUpdate());
    }
}
