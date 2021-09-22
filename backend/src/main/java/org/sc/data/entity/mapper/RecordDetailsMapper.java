package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.FileDetails;
import org.sc.data.model.RecordDetails;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.data.model.FileDetails.*;

@Component
public class RecordDetailsMapper implements Mapper<RecordDetails> {
    private static final Logger LOGGER = getLogger(RecordDetailsMapper.class);

    @Override
    public RecordDetails mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new RecordDetails(document.getDate(UPLOADED_ON),
                document.getString(UPLOADED_BY),
                document.getString(ON_INSTANCE),
                document.getString(REALM));
    }

    @Override
    public Document mapToDocument(final RecordDetails object) {
        LOGGER.trace("mapToDocument RecordDetails: {} ", object);
        return new Document()
                .append(UPLOADED_ON, object.getUploadedOn())
                .append(UPLOADED_BY, object.getUploadedBy())
                .append(ON_INSTANCE, object.getOnInstance())
                .append(REALM, object.getRealm());
    }
}
