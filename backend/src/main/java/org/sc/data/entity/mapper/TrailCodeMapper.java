package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.Trail;
import org.springframework.stereotype.Component;

@Component
public class TrailCodeMapper implements Mapper<String> {

    @Override
    public String mapToObject(Document document) {
        return document.getString(Trail.CODE);
    }

    @Override
    public Document mapToDocument(String object) {
        throw new IllegalStateException();
    }
}
