package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.FileDetails;
import org.sc.data.model.RecordDetails;
import org.springframework.stereotype.Component;

import static org.sc.data.model.FileDetails.*;

@Component
public class RecordDetailsMapper implements Mapper<RecordDetails> {

    @Override
    public RecordDetails mapToObject(final Document document) {
        return new RecordDetails(document.getDate(UPLOADED_ON),
                document.getString(UPLOADED_BY),
                document.getString(ON_INSTANCE),
                document.getString(REALM));
    }

    @Override
    public Document mapToDocument(final RecordDetails object) {
        return new Document()
                .append(UPLOADED_ON, object.getUploadedOn())
                .append(UPLOADED_BY, object.getUploadedBy())
                .append(ON_INSTANCE, object.getOnInstance())
                .append(REALM, object.getRealm());
    }
}
