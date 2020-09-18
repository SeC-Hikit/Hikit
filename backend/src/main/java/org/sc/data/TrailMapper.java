package org.sc.data;

import com.google.inject.Inject;
import org.bson.Document;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TrailMapper implements Mapper<Trail> {

    private final PositionMapper positionMapper;
    private final CoordinatesAltitudeMapper coordinatesAltitudeMapper;

    @Inject
    public TrailMapper(PositionMapper positionMapper,
                       CoordinatesAltitudeMapper coordinatesAltitudeMapper) {
        this.positionMapper = positionMapper;
        this.coordinatesAltitudeMapper = coordinatesAltitudeMapper;
    }

    @Override
    public Trail mapToObject(Document doc) {
        return Trail.TrailBuilder.aTrail()
                .withName(doc.getString(Trail.NAME))
                .withPostCodes(doc.get(Trail.POST_CODE, List.class))
                .withDescription(doc.getString(Trail.DESCRIPTION))
                .withCode(doc.getString(Trail.CODE))
                .withStartPos(getPos(doc, Trail.START_POS))
                .withFinalPos(getPos(doc, Trail.FINAL_POS))
                .withTrackLength(doc.getDouble(Trail.TRACK_LENGTH))
                .withEta(doc.getDouble(Trail.ETA))
                .withClassification(getClassification(doc))
                .withCountry(doc.getString(Trail.COUNTRY))
                .withCoordinates(getCoordinatesWithAltitude(doc))
                .withLastUpdate(getLastUpdateDate(doc))
                .build();
    }

    @Override
    public Document mapToDocument(Trail object) {
        return new Document(Trail.NAME, object.getName())
                .append(Trail.DESCRIPTION, object.getDescription())
                .append(Trail.CODE, object.getCode())
                .append(Trail.POST_CODE, object.getPostCodes())
                .append(Trail.START_POS, object.getStartPos())
                .append(Trail.FINAL_POS, object.getFinalPos())
                .append(Trail.TRACK_LENGTH, object.getTrackLength())
                .append(Trail.ETA, object.getEta())
                .append(Trail.CLASSIFICATION, object.getTrailClassification().toString())
                .append(Trail.COUNTRY, object.getCountry())
                .append(Trail.GEO_POINTS, object.getCoordinates().stream().map(coordinatesAltitudeMapper::mapToDocument).collect(toList()));
    }

    private List<CoordinatesWithAltitude> getCoordinatesWithAltitude(Document doc) {
        final List<Document> list = doc.get(Trail.GEO_POINTS, List.class);
        return list.stream().map(coordinatesAltitudeMapper::mapToObject).collect(toList());
    }


    private Position getPos(Document doc, String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return positionMapper.mapToObject(pos);
    }

    private Date getLastUpdateDate(Document doc) {
//        TODO
        return null;
    }

    private TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }

}
