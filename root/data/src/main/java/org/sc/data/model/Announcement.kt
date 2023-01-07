package org.sc.data.model

data class Announcement(
        val id: String,
        val name: String,
        val description: String,
        val relatedTopic: AnnouncementRelatedTopic,
        val type: AnnouncementType,
        val valid: Boolean,
        val recordDetails: RecordDetails
) {

    companion object {
        const val COLLECTION_NAME = "core.Announcement"

        const val ID = "_id"
        const val RELATED_TOPIC = "relatedTopic"
        const val TYPE = "type"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val IS_VALID = "isVisible"
        const val RECORD_DETAILS = "recordDetails"
    }

}
