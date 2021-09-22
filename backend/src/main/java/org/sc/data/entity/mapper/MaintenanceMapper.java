package org.sc.data.entity.mapper;

import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.sc.data.model.Maintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class MaintenanceMapper implements Mapper<Maintenance> {
    private static final Logger LOGGER = getLogger(MaintenanceMapper.class);

    private final RecordDetailsMapper recordDetailsMapper;

    @Autowired
    public MaintenanceMapper(RecordDetailsMapper recordDetailsMapper) {
        this.recordDetailsMapper = recordDetailsMapper;
    }

    @Override
    public Maintenance mapToObject(Document document) {
        LOGGER.trace("mapToObject Document: {} ", document);
        return new Maintenance(
                document.getString(Maintenance.OBJECT_ID),
                document.getDate(Maintenance.DATE),
                document.getString(Maintenance.TRAIL_ID),
                document.getString(Maintenance.MEETING_PLACE),
                document.getString(Maintenance.DESCRIPTION),
                document.getString(Maintenance.CONTACT),
                recordDetailsMapper.mapToObject(
                        document.get(Maintenance.RECORD_DETAILS, Document.class)
                ));
    }

    @Override
    public Document mapToDocument(Maintenance object) {
        LOGGER.trace("mapToDocument Maintenance: {} ", object);
        return new Document(Maintenance.DATE, object.getDate())
                .append(Maintenance.TRAIL_ID, object.getTrailId())
                .append(Maintenance.CONTACT, object.getContact())
                .append(Maintenance.DESCRIPTION, object.getDescription())
                .append(Maintenance.MEETING_PLACE, object.getMeetingPlace())
                .append(Maintenance.RECORD_DETAILS, object.getRecordDetails());
    }
}
