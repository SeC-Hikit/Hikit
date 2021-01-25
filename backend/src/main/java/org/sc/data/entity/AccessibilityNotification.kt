package org.sc.data.entity

import java.util.*

data class AccessibilityNotification (val _id: String, val description: String, val code: String, val reportDate: Date,
                                      val resolutionDate: Date, val isMinor: Boolean, val resolution: String) {
    companion object {
        const val COLLECTION_NAME = "core.AccessibilityNotifications"

        const val OBJECT_ID = "_id"
        const val TRAIL_CODE = "code"
        const val DESCRIPTION = "description"
        const val REPORT_DATE = "reportDate"
        const val RESOLUTION_DATE = "resolutionDate"
        const val IS_MINOR = "isMinor"
        const val RESOLUTION = "resolution"
    }
}