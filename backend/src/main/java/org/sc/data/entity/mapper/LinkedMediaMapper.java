package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.entity.KeyVal;
import org.sc.data.entity.LinkedMedia;
import org.sc.data.entity.Poi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class LinkedMediaMapper implements Mapper<LinkedMedia> {

    private final KeyValMapper keyValMapper;

    @Autowired
    public LinkedMediaMapper(KeyValMapper keyValMapper) {
        this.keyValMapper = keyValMapper;
    }


    @Override
    public LinkedMedia mapToObject(Document document) {
        return new LinkedMedia(document.getString(LinkedMedia.ID),
                document.getString(LinkedMedia.DESCRIPTION),
                getKeyVals(document)
        );
    }

    @Override
    public Document mapToDocument(LinkedMedia object) {
        return new Document(LinkedMedia.ID, object.getId())
                .append(LinkedMedia.DESCRIPTION, object.getDescription())
                .append(LinkedMedia.KEY_VAL,
                        object.getKeyValList().stream()
                                .map(keyValMapper::mapToDocument)
                                .collect(toList()));
    }

    private List<KeyVal> getKeyVals(Document document) {
        List<Document> list = document.getList(Poi.KEY_VAL, Document.class);
        return list.stream().map(keyValMapper::mapToObject).collect(toList());
    }
}
