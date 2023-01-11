package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sc.data.model.AnnouncementType;
import org.sc.data.model.RecordDetails;

@Data
@AllArgsConstructor
public class AnnouncementDto {
    private String id;
    private String name;
    private String description;
    private AnnouncementRelatedTopicDto relatedTopic;
    private AnnouncementType type;
    private boolean valid;
    private RecordDetails recordDetails;
}
