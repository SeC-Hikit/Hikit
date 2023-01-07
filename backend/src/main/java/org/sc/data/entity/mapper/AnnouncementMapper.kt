package org.sc.data.entity.mapper

import org.bson.Document
import org.sc.data.model.Announcement
import org.sc.data.model.AnnouncementType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AnnouncementMapper @Autowired constructor(private val announcementTopicTypeMapper: AnnouncementTopicTypeMapper,
                                                private val recordDetailsMapper: RecordDetailsMapper) : Mapper<Announcement> {

    override fun mapToObject(document: Document) =
            Announcement(
                    id = document.getString(Announcement.ID),
                    name = document.getString(Announcement.NAME),
                    description = document.getString(Announcement.DESCRIPTION),
                    relatedTopic = announcementTopicTypeMapper.mapToObject(
                            document.get(Announcement.RELATED_TOPIC, Document::class.java)
                    ),
                    type = getType(document),
                    valid = document.getBoolean(Announcement.IS_VALID),
                    recordDetails = recordDetailsMapper.mapToObject(
                            document.get(Announcement.RECORD_DETAILS, Document::class.java)))


    override fun mapToDocument(announcement: Announcement): Document =
            Document(Announcement.ID, announcement.id)
                    .append(Announcement.NAME, announcement.name)
                    .append(Announcement.DESCRIPTION, announcement.description)
                    .append(Announcement.RELATED_TOPIC,
                            announcementTopicTypeMapper.mapToDocument(announcement.relatedTopic))
                    .append(Announcement.TYPE, announcement.type.name)
                    .append(Announcement.IS_VALID, announcement.valid)
                    .append(Announcement.RECORD_DETAILS,
                            recordDetailsMapper.mapToDocument(announcement.recordDetails))

    private fun getType(document: Document): AnnouncementType =
            AnnouncementType.valueOf(document.getString(Announcement.TYPE))
}