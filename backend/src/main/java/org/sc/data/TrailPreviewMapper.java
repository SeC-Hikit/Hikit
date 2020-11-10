package org.sc.data;

import com.google.inject.Inject;
import org.bson.Document;
import org.sc.common.rest.controller.Position;
import org.sc.common.rest.controller.Trail;
import org.sc.common.rest.controller.TrailClassification;
import org.sc.common.rest.controller.TrailPreview;

public class TrailPreviewMapper implements Mapper<TrailPreview>{

    private final PositionMapper positionMapper;

    @Inject
    public TrailPreviewMapper(final PositionMapper positionMapper) {
        this.positionMapper = positionMapper;
    }

    @Override
    public TrailPreview mapToObject(Document doc) {
        return new TrailPreview(doc.getString(Trail.CODE), getClassification(doc),
                getPos(doc, Trail.START_POS),
                getPos(doc, Trail.FINAL_POS));
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
