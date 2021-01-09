package org.sc.data.entity

import org.sc.common.rest.PoiMacroType
import org.sc.common.rest.TrailCoordinates
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import java.util.*

data class Poi (val id: String,
                val name: String,
                val description: String,
                val tags: List<String>,
                val macroType: PoiMacroType,
                val microType: List<String>,
                val mediaIds: List<String>,
                val trailCodes: List<String>,
                val trailCoordinates: TrailCoordinates,
                val createdOn: Date,
                val lastUpdatedOn: Date){
    companion object {

        const val COLLECTION_NAME = "core.Poi"

        const val OBJECT_ID = "_id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val TAGS = "tags"
        const val MACROTYPE = "macrotype"
        const val MICROTYPES = "microtypes"
        const val MEDIA_IDS = "mediaIds"
        const val TRAIL_CODES = "trailIds"
        const val TRAIL_COORDINATES = "trailCoordinates"
        const val CREATED_ON = "trailCoordinates"
        const val LAST_UPDATE_ON = "lastUpdateOn"
    }
}