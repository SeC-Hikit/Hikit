package org.sc.data.model

data class AnnouncementRelatedTopic(
        val announcementTopicType: AnnouncementTopicType,
        val id: String
) {
    companion object {
        const val ID = "_id"
        const val ANNOUNCEMENT_TOPIC_TYPE = "announcementTopicType"
    }
}