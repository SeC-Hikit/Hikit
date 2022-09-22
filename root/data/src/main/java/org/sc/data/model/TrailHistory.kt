package org.sc.data.model

import java.util.Date

data class TrailHistory(val id: String, val data: Trail, val inserted: Date) {
    companion object {
        const val ID = "_id"
        const val DATA = "data"
        const val INSERTED = "date"
    }
}
