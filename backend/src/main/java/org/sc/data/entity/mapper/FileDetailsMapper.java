package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.FileDetails;
import org.springframework.stereotype.Component;

import static org.sc.data.model.FileDetails.*;

@Component
public class FileDetailsMapper implements Mapper<FileDetails> {

    @Override
    public FileDetails mapToObject(final Document document) {
        return new FileDetails(document.getDate(UPLOADED_ON),
                document.getString(UPLOADED_BY),
                document.getString(ON_INSTANCE),
                document.getString(REALM),
                document.getString(FILENAME),
                document.getString(ORIGINAL_FILENAME));
    }

    @Override
    public Document mapToDocument(final FileDetails object) {
        return new Document()
                .append(UPLOADED_ON, object.getUploadedOn())
                .append(UPLOADED_BY, object.getUploadedBy())
                .append(ON_INSTANCE, object.getOnInstance())
                .append(REALM, object.getRealm())
                .append(FILENAME, object.getFilename())
                .append(ORIGINAL_FILENAME, object.getOriginalFilename());
    }
}
