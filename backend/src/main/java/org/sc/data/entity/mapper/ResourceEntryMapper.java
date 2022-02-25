package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.ResourceEntry;
import org.springframework.stereotype.Component;

@Component
public class ResourceEntryMapper implements Mapper<ResourceEntry> {

    @Override
    public ResourceEntry mapToObject(Document document) {
        return new ResourceEntry(
                document.getString(ResourceEntry.OBJECT_ID),
                document.getString(ResourceEntry.INSTANCE_ID),
                document.getString(ResourceEntry.ENTRY_TYPE),
                document.getString(ResourceEntry.ENTRY_ID),
                document.getString(ResourceEntry.TARGETING_TRAIL),
                document.getString(ResourceEntry.ACTION),
                document.getDate(ResourceEntry.CREATED_ON),
                document.getString(ResourceEntry.USER_PROMPTING)
        );
    }

    @Override
    public Document mapToDocument(ResourceEntry object) {
        return new Document(ResourceEntry.OBJECT_ID, object.getId())
                .append(ResourceEntry.INSTANCE_ID, object.getInstanceId())
                .append(ResourceEntry.TARGETING_TRAIL, object.getTargetingTrail())
                .append(ResourceEntry.ENTRY_TYPE, object.getEntryType())
                .append(ResourceEntry.ENTRY_ID, object.getEntryId())
                .append(ResourceEntry.CREATED_ON, object.getCreatedOn())
                .append(ResourceEntry.ACTION, object.getAction())
                .append(ResourceEntry.USER_PROMPTING, object.getUserPrompting());
    }
}
