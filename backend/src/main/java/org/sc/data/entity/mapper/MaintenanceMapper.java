package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.entity.Maintenance;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceMapper implements Mapper<Maintenance> {

    @Override
    public Maintenance mapToObject(Document document) {
        return new Maintenance(
                document.getString(Maintenance.OBJECT_ID),
                document.getDate(Maintenance.DATE),
                document.getString(Maintenance.TRAIL_CODE),
                document.getString(Maintenance.MEETING_PLACE),
                document.getString(Maintenance.DESCRIPTION),
                document.getString(Maintenance.CONTACT));
    }

    @Override
    public Document mapToDocument(Maintenance object) {
        return new Document(Maintenance.DATE, object.getDate())
                .append(Maintenance.TRAIL_CODE, object.getCode())
                .append(Maintenance.CONTACT, object.getContact())
                .append(Maintenance.DESCRIPTION, object.getDescription())
                .append(Maintenance.MEETING_PLACE, object.getMeetingPlace());
    }
}
