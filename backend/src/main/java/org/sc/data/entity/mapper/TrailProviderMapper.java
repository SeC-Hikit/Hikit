package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.Poi;
import org.sc.data.model.TrailProvider;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class TrailProviderMapper implements Mapper<TrailProvider> {
    private static final Logger LOGGER = getLogger(TrailProviderMapper.class);

    private final KeyValMapper keyValMapper;

    public TrailProviderMapper(KeyValMapper keyValMapper) {
        this.keyValMapper = keyValMapper;
    }

    @Override
    public TrailProvider mapToObject(Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new TrailProvider(document.getString(TrailProvider.ID),
                document.getString(TrailProvider.NAME),
                document.getString(TrailProvider.PARENT_ID),
                document.getString(TrailProvider.DESCRIPTION),
                document.getBoolean(TrailProvider.PUBLIC_PRIVATE),
                document.getList(Poi.KEY_VAL, Document.class).stream().map(keyValMapper::mapToObject).collect(toList()));
    }

    @Override
    public Document mapToDocument(TrailProvider object) {
        LOGGER.trace("mapToDocument TrailProvider: {} ", object);
        return new Document(TrailProvider.ID, object.getId())
                .append(TrailProvider.NAME, object.getName())
                .append(TrailProvider.DESCRIPTION, object.getDescription())
                .append(TrailProvider.PUBLIC_PRIVATE, object.isPublicOrganization())
                .append(TrailProvider.PARENT_ID, object.getParentId())
                .append(TrailProvider.KEY_VAL,
                        object.getKeyVal().stream()
                                .map(keyValMapper::mapToDocument)
                                .collect(toList()));
    }
}
