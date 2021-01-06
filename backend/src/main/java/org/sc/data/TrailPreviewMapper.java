package org.sc.data;

import org.bson.Document;
import org.sc.common.rest.Position;
import org.sc.common.rest.Trail;
import org.sc.common.rest.TrailClassification;
import org.sc.common.rest.TrailPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrailPreviewMapper implements Mapper<TrailPreview>{

    private final PositionMapper positionMapper;

    @Autowired
    public TrailPreviewMapper(final PositionMapper positionMapper) {
        this.positionMapper = positionMapper;
    }

    @Override
    public TrailPreview mapToObject(Document doc) {
        return new TrailPreview(doc.getString(Trail.CODE), getClassification(doc),
                getPos(doc, Trail.START_POS),
                getPos(doc, Trail.FINAL_POS),
                doc.getDate(Trail.LAST_UPDATE_DATE));
    }

    @Override
    public Document mapToDocument(TrailPreview object) {
        throw new IllegalStateException();
    }

    private Position getPos(final Document doc,
                            final String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return positionMapper.mapToObject(pos);
    }

    private TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }
}
