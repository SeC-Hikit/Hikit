package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.entity.Media;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper implements Mapper<Media> {
    @Override
    public Media mapToObject(final Document document) {
        return new Media(
                document.getString(Media.OBJECT_ID),
                document.getDate(Media.CREATION_DATE),
                document.getString(Media.NAME),
                document.getString(Media.FILENAME),
                document.getString(Media.FILE_URL),
                document.getString(Media.MIME),
                document.getString(Media.FILE_SIZE));
    }

    @Override
    public Document mapToDocument(final Media object) {
        return new Document(Media.OBJECT_ID, object.get_id())
                .append(Media.CREATION_DATE, object.getCreationDate())
                .append(Media.NAME, object.getName())
                .append(Media.FILENAME, object.getFileName())
                .append(Media.FILE_URL, object.getFileUrl())
                .append(Media.MIME, object.getMime())
                .append(Media.FILE_SIZE, object.getFileSize());
    }
}
