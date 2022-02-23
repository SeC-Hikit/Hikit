package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.TrailMapping;
import org.springframework.stereotype.Component;

@Component
public class TrailMappingMapper implements Mapper<TrailMapping> {

    @Override
    public TrailMapping mapToObject(final Document document) {
        return new TrailMapping(
                document.getString(TrailMapping.ID),
                document.getString(TrailMapping.NAME),
                document.getString(TrailMapping.CODE)
        );
    }

    @Override
    public Document mapToDocument(TrailMapping object) {
        throw new IllegalStateException();
    }
}
