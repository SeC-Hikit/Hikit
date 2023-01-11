package org.sc.data.entity.mapper

import org.bson.Document
import org.sc.data.model.AnnouncementRelatedTopic
import org.sc.data.model.AnnouncementTopicType
import org.springframework.stereotype.Component

@Component
class AnnouncementTopicTypeMapper : Mapper<AnnouncementRelatedTopic> {

    override fun mapToObject(document: Document): AnnouncementRelatedTopic =
            AnnouncementRelatedTopic(getType(document), document.getString(AnnouncementRelatedTopic.ID))

    override fun mapToDocument(announcementTopicType: AnnouncementRelatedTopic) =
        Document(AnnouncementRelatedTopic.ID, announcementTopicType.id)
                .append(AnnouncementRelatedTopic.ANNOUNCEMENT_TOPIC_TYPE,
                        announcementTopicType.announcementTopicType.name)

    private fun getType(doc: Document): AnnouncementTopicType {
        val classification = doc.getString(AnnouncementRelatedTopic.ANNOUNCEMENT_TOPIC_TYPE)
        return AnnouncementTopicType.values()
                .first { it.toString() == classification }
    }

}