package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.entity.KeyVal;
import org.springframework.stereotype.Component;

@Component
public class KeyValMapper implements Mapper<KeyVal> {

    @Override
    public KeyVal mapToObject(Document document) {
        return new KeyVal(document.getString(KeyVal.KEY),
                document.getString(KeyVal.VAL));
    }

    @Override
    public Document mapToDocument(KeyVal object) {
        return new Document(KeyVal.KEY, object.getKey())
                .append(KeyVal.VAL, object.getValue());
    }
}
