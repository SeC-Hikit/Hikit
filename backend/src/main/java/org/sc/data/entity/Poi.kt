package org.sc.data.entity

import org.sc.common.rest.PoiMacroType
import org.sc.common.rest.TrailCoordinates
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import java.util.*

data class Poi (@Id val id: String,
                @Indexed(direction = IndexDirection.ASCENDING)
                val name: String,
                val description: String,
                @Indexed(direction = IndexDirection.ASCENDING)
                val tags: List<String>,
                @Indexed(direction = IndexDirection.ASCENDING)
                val macroType: PoiMacroType,
                val microType: List<String>,
                val mediaIds: List<String>,
                val trailIds: List<String>,
                val trailCoordinates: TrailCoordinates,
                val createdOn: Date,
                val lastUpdatedOn: Date)