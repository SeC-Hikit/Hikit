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
                .withDescription(doc.getString(Trail.DESCRIPTION))
                .withCode(doc.getString(Trail.CODE))
                .withStartPos(getPos(doc, Trail.START_POS))
                .withFinalPos(getPos(doc, Trail.FINAL_POS))
                .withTrackLength(doc.getDouble(Trail.TRACK_LENGTH))
                .withEta(doc.getDouble(Trail.ETA))
                .withTrailClassification(getClassification(doc))
                .withCountry(doc.getString(Trail.COUNTRY))
                .withCoordinates(getCoordinatesWithAltitude(doc))
                .withDate(getLastUpdateDate(doc))
                .withMaintainingSection(doc.getString(Trail.SECTION_CARED_BY))
                .build();
    }

    @Override
    public Document mapToDocument(Trail object) {
        return new Document(
                Trail.NAME, object.getName())
                .append(Trail.DESCRIPTION, object.getDescription())
                .append(Trail.CODE, object.getCode())
                .append(Trail.START_POS, positionMapper.mapToDocument(object.getStartPos()))
                .append(Trail.FINAL_POS, positionMapper.mapToDocument(object.getFinalPos()))
                .append(Trail.TRACK_LENGTH, object.getTrackLength())
                .append(Trail.ETA, object.getEta())
                .append(Trail.CLASSIFICATION, object.getClassification().toString())
                .append(Trail.COUNTRY, object.getCountry())
                .append(Trail.SECTION_CARED_BY, object.getMaintainingSection())
                .append(Trail.LAST_UPDATE_DATE, new Date())
                .append(Trail.COORDINATES, object.getCoordinates()
                        .stream().map(coordinatesAltitudeMapper::mapToDocument).collect(toList()));
    }

    private List<CoordinatesWithAltitude> getCoordinatesWithAltitude(Document doc) {
        final List<Document> list = doc.get(Trail.COORDINATES, List.class);
        return list.stream().map(coordinatesAltitudeMapper::mapToObject).collect(toList());
    }


    private Position getPos(Document doc, String fieldName) {
        final Document pos = doc.get(fieldName, Document.class);
        return positionMapper.mapToObject(pos);
    }

    private Date getLastUpdateDate(Document doc) {
        return doc.getDate(Trail.LAST_UPDATE_DATE);
    }

    private TrailClassification getClassification(Document doc) {
        final String classification = doc.getString(Trail.CLASSIFICATION);
        return TrailClassification.valueOf(classification);
    }

}
