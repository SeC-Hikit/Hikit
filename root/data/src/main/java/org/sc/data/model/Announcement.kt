package org.sc.data.model

data class Announcement(
        var id: String,
        var name: String,
        var description: String,
        var relatedTopic: AnnouncementRelatedTopic,
        var type: AnnouncementType,
        var valid: Boolean,
        var recordDetails: RecordDetails
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
