package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sc.data.model.AnnouncementTopicType;
@Data
@AllArgsConstructor
public class AnnouncementRelatedTopicDto {
    private AnnouncementTopicType announcementTopicType;
    private String id;
}
