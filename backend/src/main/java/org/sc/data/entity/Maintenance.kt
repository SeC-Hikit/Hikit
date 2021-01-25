package org.sc.data.entity

import java.util.Date

data class Maintenance (val _id : String,
                        val date : Date,
                        val code : String,
                        val meetingPlace : String,
                        val description : String,
                        val contact : String) {
    companion object {
        const val COLLECTION_NAME = "core.Maintenance"
        const val OBJECT_ID = "_id"
        const val TRAIL_CODE = "code"
        const val DATE = "date"
        const val DESCRIPTION = "description"
        const val CONTACT = "contact"
        const val MEETING_PLACE = "meetingPlace"
    }
}