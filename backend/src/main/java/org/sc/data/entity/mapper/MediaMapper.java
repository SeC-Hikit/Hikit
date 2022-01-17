package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class MediaMapper implements Mapper<Media> {
    private static final Logger LOGGER = getLogger(MediaMapper.class);

    private final FileDetailsMapper fileDetailsMapper;

    @Autowired
    public MediaMapper(FileDetailsMapper fileDetailsMapper) {
        this.fileDetailsMapper = fileDetailsMapper;
    }

    @Override
    public Media mapToObject(final Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new Media(
                document.getString(Media.OBJECT_ID),
                document.getDate(Media.CREATION_DATE),
                document.getString(Media.NAME),
                document.getString(Media.FILENAME),
                document.getString(Media.EXTENSION),
                document.getString(Media.FILE_URL),
                document.getString(Media.MIME),
                document.getLong(Media.FILE_SIZE),
                fileDetailsMapper.mapToObject(document.get(Media.RECORD_DETAILS, Document.class)),
                document.getBoolean(Media.IS_COMPRESSED),
                document.getList(Media.RESOLUTIONS, String.class));
    }

    @Override
    public Document mapToDocument(final Media object) {
        LOGGER.trace("mapToDocument Media: {} ", object);
        return new Document()
                .append(Media.CREATION_DATE, object.getCreationDate())
                .append(Media.NAME, object.getName())
                .append(Media.FILENAME, object.getFileName())
                .append(Media.EXTENSION, object.getExtension())
                .append(Media.FILE_URL, object.getFileUrl())
                .append(Media.MIME, object.getMime())
                .append(Media.FILE_SIZE, object.getFileSize())
                .append(Media.RECORD_DETAILS, fileDetailsMapper.mapToDocument(object.getFileDetails()))
                .append(Media.IS_COMPRESSED, object.isCompressed())
                .append(Media.RESOLUTIONS, object.getResolutions());
    }
}
